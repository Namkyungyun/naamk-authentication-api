package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.common.TbUsers;
import kr.co.naamk.naamkauthenticationapi.domain.community.TbPenaltyHist;
import kr.co.naamk.naamkauthenticationapi.domain.community.TbPost;
import kr.co.naamk.naamkauthenticationapi.domain.community.TbReportsHist;
import kr.co.naamk.naamkauthenticationapi.domain.type.PenaltyType;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.PenaltyHistMapper;
import kr.co.naamk.naamkauthenticationapi.web.dto.PenaltyHistDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.PenaltyHistRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.PostRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.ReportHistRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PenaltyHistService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PenaltyHistRepository penaltyHistRepository;
    private final ReportHistRepository reportHistRepository;

    @Transactional(readOnly = true)
    public Page< PenaltyHistDto > findUserPenaltyHistListByUserId( Long linkedId, String type, Pageable pageable ) throws ServiceException {

        Long id = null;
        if ( type.equals( PenaltyType.user.name() ) ) {
            TbUsers user = userRepository.findById( linkedId )
                    .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "user not found" ) );
            id = user.getId();
        } else if ( type.equals( PenaltyType.post.name() ) ) {
            TbPost post = postRepository.findById( linkedId )
                    .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "post not found" ) );
            id = post.getId();
        } else {
            throw new ServiceException( ServiceMessageType.NOT_FOUND, "type not supported" );
        }

        pageable = PageRequest.of( Math.max( pageable.getPageNumber(), 0 ), pageable.getPageSize(), pageable.getSort() );
        return penaltyHistRepository.findPenaltyHistsByLinkedIdAndType( id, type, pageable );
    }

    @Transactional(rollbackFor = Exception.class)
    public PenaltyHistDto.CreateResponse saveUserPenalty( Long linkedId, String type, PenaltyHistDto.CreateRequest dto ) throws ServiceException {
        // check existing user or post id
        Long id = null;
        String username = null;
        if ( type.equals( PenaltyType.user.name() ) ) {
            TbUsers user = userRepository.findById( linkedId )
                    .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "user not found" ) );
            id = user.getId();
            username = user.getUsername();
        } else if ( type.equals( PenaltyType.post.name() ) ) {
            TbPost post = postRepository.findById( linkedId )
                    .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "post not found" ) );
            TbUsers user = userRepository.findById( post.getUserId() )
                    .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "user not found" ) );

            id = post.getId();
            username = user.getUsername();
        } else {
            throw new ServiceException( ServiceMessageType.NOT_FOUND, "type not supported" );
        }

        // penalty create
        TbPenaltyHist entity = PenaltyHistMapper.INSTANCE.toEntity( dto );
        entity.setLinkedId( id );
        entity.setType( type );

        TbPenaltyHist savedEntity = penaltyHistRepository.save( entity );

        // report update [접수처리 완료]
        List< TbReportsHist > uncompletedReports = reportHistRepository.findActiveReportsByLinkedIdAndType( linkedId, type );
        if ( !uncompletedReports.isEmpty() ) {
            uncompletedReports.forEach( el -> el.setIsActive( false ) );
            reportHistRepository.saveAll( uncompletedReports );
        }

        return PenaltyHistDto.CreateResponse.builder()
                .username( username )
                .linkedId( savedEntity.getLinkedId() )
                .type( savedEntity.getType() )
                .isActive( savedEntity.getIsActive() )
                .description( savedEntity.getDescription() )
                .build();
    }

    @Transactional(readOnly = true)
    public TbUsers getUserById( Long userId ) {
        return userRepository.findById( userId )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "user not found" ) );
    }
}
