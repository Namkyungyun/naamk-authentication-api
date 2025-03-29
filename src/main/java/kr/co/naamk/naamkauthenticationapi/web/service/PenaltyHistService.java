package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.common.TbUsers;
import kr.co.naamk.naamkauthenticationapi.domain.community.TbPenaltyHist;
import kr.co.naamk.naamkauthenticationapi.domain.type.PenaltyType;
import kr.co.naamk.naamkauthenticationapi.exception.ServiceException;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.mapstruct.PenaltyHistMapper;
import kr.co.naamk.naamkauthenticationapi.web.dto.PenaltyHistDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.PenaltyHistRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.ReportHistRepository;
import kr.co.naamk.naamkauthenticationapi.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PenaltyHistService {
    private final UserRepository userRepository;
    private final PenaltyHistRepository penaltyHistRepository;
    private final ReportHistRepository reportHistRepository;

    @Transactional(readOnly = true)
    public List<PenaltyHistDto> getHistByIdAndType(Long userId, String type) {
        TbUsers user = userRepository.findById( userId )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "user not found" ) );

        List< TbPenaltyHist > entities = penaltyHistRepository.findByLinkedIdAndTypeOrderByCreatedAtDesc( user.getId(), type );
        List< PenaltyHistDto > dtoList = PenaltyHistMapper.INSTANCE.toDtoList( entities );
        Boolean isExistReport = reportHistRepository.existsByLinkedIdAndType( user.getId(), type );

        dtoList.forEach( dto -> {dto.setIsExistReport( isExistReport );} );

        return dtoList;
    }

    @Transactional(rollbackFor = Exception.class)
    public PenaltyHistDto saveUserPenalty( Long userId, PenaltyHistDto.CreateRequest dto) throws ServiceException {
        TbUsers user = userRepository.findById( userId )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "user not found" ) );

        TbPenaltyHist entity = PenaltyHistMapper.INSTANCE.toEntity( dto );
        entity.setLinkedId( user.getId() );
        entity.setType( PenaltyType.user.name() );

        TbPenaltyHist savedEntity = penaltyHistRepository.save( entity );

        return PenaltyHistMapper.INSTANCE.toDto( savedEntity );
    }
}
