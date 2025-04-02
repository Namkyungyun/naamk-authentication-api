package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.common.TbUsers;
import kr.co.naamk.naamkauthenticationapi.domain.community.TbReportsHist;
import kr.co.naamk.naamkauthenticationapi.domain.type.SearchCommon;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.UserPenaltyMapper;
import kr.co.naamk.naamkauthenticationapi.utils.AESGCMEncryptionUtil;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserPenaltyDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.ReportHistRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPenaltyService {

    private final ReportHistRepository reportHistRepository;
    private final AESGCMEncryptionUtil aesGCMEncryptionUtil;
    private final UserRepository userRepository;

    public UserPenaltyDto.SearchOption getSearch( ) {
        SearchCommon searchCommon = new SearchCommon();

        return UserPenaltyDto.SearchOption.builder()
                .reportStatus( searchCommon.reportStatus )
                .penaltyStatus( searchCommon.penaltyStatus )
                .build();
    }

    @Transactional(readOnly = true)
    public Page< UserPenaltyDto > findUserList( UserPenaltyDto.SearchRequest searchRequest, Pageable pageable ) {

        Timestamp startDate = null;
        Timestamp endDate = null;
        if ( searchRequest.getStartDate() != null && searchRequest.getEndDate() != null ) {
            startDate = Timestamp.from( searchRequest.getStartDate().toInstant() );
            endDate = Timestamp.from( searchRequest.getEndDate().plusDays( 1 ).toInstant() );
        }

        pageable = PageRequest.of( Math.max( pageable.getPageNumber(), 0 ), pageable.getPageSize() );


        Page< Map<String, Object> > page = reportHistRepository.findAllReportsWithUserAndPenalty(
                searchRequest.getReportStatus(),
                searchRequest.getPenaltyStatus(),
                searchRequest.getReportedName(),
                searchRequest.getPenaltyCreatedBy(),
                startDate,
                endDate,
                pageable
              );

        List< UserPenaltyDto > contents = UserPenaltyMapper.INSTANCE.toUserPenaltyDtoList( page.getContent() );


        return new PageImpl<>( contents, pageable, contents.size() );
    }


    @Transactional(rollbackFor = Exception.class)
    public Object createUserReport(UserPenaltyDto.CreateRequest dto) {

        TbReportsHist entity = UserPenaltyMapper.INSTANCE.toEntity( dto );
        entity.setIsActive( true );
        TbReportsHist save = reportHistRepository.save( entity );

        return save;
    }


    @Transactional(readOnly = true)
    public UserPenaltyDto.UserDetailResponse findUserById( Long userId ) {
        TbUsers user = userRepository.findById( userId )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "not found user" ) );

        Map<String, Object> map = reportHistRepository.findReportWithPenaltyAndUser( user.getId() , "user")
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "not found report" ) );;

        return UserPenaltyMapper.INSTANCE.toUserPenaltyDetailResponse( map );
    }

    @Transactional(readOnly = true)
    public Page< UserPenaltyDto.ReportHistResponse > findUserReportHist( Long userId, Pageable pageable ) {
        TbUsers user = userRepository.findById( userId )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "not found user" ) );

        pageable = PageRequest.of( Math.max( pageable.getPageNumber(), 0 ), pageable.getPageSize() );


        Page< Map<String, Object> > page = reportHistRepository.findReportHists(
                user.getId(),
                "user",
                pageable
        );

        List< UserPenaltyDto.ReportHistResponse > contents = UserPenaltyMapper.INSTANCE.toReportHistResponseList( page.getContent() );


        return new PageImpl<>( contents, pageable, contents.size() );

    }


}
