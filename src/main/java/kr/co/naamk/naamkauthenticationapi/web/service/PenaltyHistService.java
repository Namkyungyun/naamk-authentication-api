package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.common.TbUsers;
import kr.co.naamk.naamkauthenticationapi.domain.community.TbPenaltyHist;
import kr.co.naamk.naamkauthenticationapi.domain.community.TbReportsHist;
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
    public List<PenaltyHistDto> findHistListByUserIdAndType( Long userId, String type) throws ServiceException {
        return  penaltyHistRepository.findPenaltyHistsByLinkedIdAndType( userId, type );
    }

    @Transactional(rollbackFor = Exception.class)
    public PenaltyHistDto saveUserPenalty( Long userId, String type, PenaltyHistDto.CreateRequest dto) throws ServiceException {
        // penalty
        TbPenaltyHist entity = PenaltyHistMapper.INSTANCE.toEntity( dto );
        entity.setLinkedId( userId );
        entity.setType( type );

        TbPenaltyHist savedEntity = penaltyHistRepository.save( entity );

        // report
        List< TbReportsHist > uncompletedReports = reportHistRepository.findActiveReportsByLinkedIdAndType( userId, type );
        uncompletedReports.forEach( el -> el.setIsActive( false ) );
        reportHistRepository.saveAll( uncompletedReports );

        return PenaltyHistMapper.INSTANCE.toDto( savedEntity );
    }

    @Transactional(readOnly = true)
    public TbUsers getUserById(Long userId) {
        return userRepository.findById( userId )
                .orElseThrow( ( ) -> new ServiceException( ServiceMessageType.NOT_FOUND, "user not found" ) );
    }
}
