package kr.co.naamk.naamkauthenticationapi.mapstruct;

import kr.co.naamk.naamkauthenticationapi.domain.community.TbPenaltyHist;
import kr.co.naamk.naamkauthenticationapi.web.dto.PenaltyHistDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PenaltyHistMapper {

    PenaltyHistMapper INSTANCE = Mappers.getMapper( PenaltyHistMapper.class );

    TbPenaltyHist toEntity( PenaltyHistDto dto);
    TbPenaltyHist toEntity( PenaltyHistDto.CreateRequest dto);

    PenaltyHistDto toDto( TbPenaltyHist entity);
    List<PenaltyHistDto> toDtoList( List<TbPenaltyHist> entity);

}
