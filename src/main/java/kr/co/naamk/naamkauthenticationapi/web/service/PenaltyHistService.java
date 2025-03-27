package kr.co.naamk.naamkauthenticationapi.web.service;

import kr.co.naamk.naamkauthenticationapi.domain.community.TbPenaltyHist;
import kr.co.naamk.naamkauthenticationapi.mapstruct.PenaltyHistMapper;
import kr.co.naamk.naamkauthenticationapi.web.dto.PenaltyHistDto;
import kr.co.naamk.naamkauthenticationapi.web.repository.PenaltyHistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PenaltyHistService {
    private final PenaltyHistRepository penaltyHistRepository;

    @Transactional(readOnly = true)
    public List<PenaltyHistDto> getHistByIdAndType(Long id, String type) {
        List< TbPenaltyHist > entities = penaltyHistRepository.findByLinkedIdAndType( id, type );

        return PenaltyHistMapper.INSTANCE.toDtoList( entities );
    }
}
