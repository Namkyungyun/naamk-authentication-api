package kr.co.naamk.naamkauthenticationapi.mapstruct.admin;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminMenus;
import kr.co.naamk.naamkauthenticationapi.web.dto.admin.AdminMenuDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AdminMenuMapper {

    AdminMenuMapper INSTANCE = Mappers.getMapper( AdminMenuMapper.class );

    TbAdminMenus createDtoToEntity( AdminMenuDto.CreateRequest dto );
    TbAdminMenus updateDtoToEntity( AdminMenuDto.UpdateRequest dto );

    AdminMenuDto toDto( TbAdminMenus entity );
    List< AdminMenuDto > toDtoList( List< TbAdminMenus > entities );

}
