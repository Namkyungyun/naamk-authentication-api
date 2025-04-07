package kr.co.naamk.naamkauthenticationapi.mapstruct.admin;

import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminUsers;
import kr.co.naamk.naamkauthenticationapi.web.dto.admin.AdminUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AdminUserMapper {

    AdminUserMapper INSTANCE = Mappers.getMapper( AdminUserMapper.class );

    TbAdminUsers ToEntity( AdminUserDto.CreateRequest dto);

    AdminUserDto toDto( TbAdminUsers entity, Timestamp expiredDate, List<String> authorities);



}
