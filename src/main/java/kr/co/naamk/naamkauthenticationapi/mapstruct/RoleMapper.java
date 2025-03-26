package kr.co.naamk.naamkauthenticationapi.mapstruct;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminRoles;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminRoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper( RoleMapper.class );

    TbAdminRoles createDtoToEntity( AdminRoleDto.CreateRequest dto );

    AdminRoleDto toDto( TbAdminRoles role );
}
