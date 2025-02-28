package kr.co.naamk.naamkauthenticationapi.mapstruct;

import kr.co.naamk.naamkauthenticationapi.domain.TbMenus;
import kr.co.naamk.naamkauthenticationapi.web.dto.MenuDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface MenuMapper {

    MenuMapper INSTANCE = Mappers.getMapper( MenuMapper.class );

    TbMenus createDtoToEntity( MenuDto.CreateRequest dto );
    TbMenus updateDtoToEntity( MenuDto.UpdateRequest dto );

    MenuDto toDto( TbMenus role );

}
