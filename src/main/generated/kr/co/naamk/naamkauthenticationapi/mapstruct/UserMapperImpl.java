package kr.co.naamk.naamkauthenticationapi.mapstruct;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminUsers;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminUserDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public TbAdminUsers ToEntity( AdminUserDto.CreateRequest dto) {
        if ( dto == null ) {
            return null;
        }

        TbAdminUsers tbAdminUsers = new TbAdminUsers();

        tbAdminUsers.setUsername( dto.getUsername() );
        tbAdminUsers.setPassword( dto.getPassword() );
        tbAdminUsers.setName( dto.getName() );
        tbAdminUsers.setEmail( dto.getEmail() );

        return tbAdminUsers;
    }

    @Override
    public AdminUserDto toDto( TbAdminUsers entity, Timestamp expiredDate, List<String> authorities) {
        if ( entity == null && expiredDate == null && authorities == null ) {
            return null;
        }

        AdminUserDto.UserDtoBuilder userDto = AdminUserDto.builder();

        if ( entity != null ) {
            userDto.id( entity.getId() );
            userDto.username( entity.getUsername() );
            userDto.name( entity.getName() );
            userDto.email( entity.getEmail() );
        }
        userDto.expiredDate( expiredDate );
        List<String> list = authorities;
        if ( list != null ) {
            userDto.authorities( new ArrayList<String>( list ) );
        }

        return userDto.build();
    }
}
