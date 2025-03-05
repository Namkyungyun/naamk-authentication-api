package kr.co.naamk.naamkauthenticationapi.mapstruct;

import kr.co.naamk.naamkauthenticationapi.domain.TbUsers;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    TbUsers ToEntity( UserDto.CreateRequest dto);

    UserDto toDto( TbUsers entity, Timestamp expiredDate, List<String> authorities);



}
