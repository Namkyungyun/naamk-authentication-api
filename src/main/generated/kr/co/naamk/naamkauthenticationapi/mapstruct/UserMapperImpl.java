package kr.co.naamk.naamkauthenticationapi.mapstruct;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.naamk.naamkauthenticationapi.domain.TbUsers;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public TbUsers ToEntity(UserDto.CreateRequest dto) {
        if ( dto == null ) {
            return null;
        }

        TbUsers tbUsers = new TbUsers();

        tbUsers.setUsername( dto.getUsername() );
        tbUsers.setPassword( dto.getPassword() );
        tbUsers.setName( dto.getName() );
        tbUsers.setEmail( dto.getEmail() );

        return tbUsers;
    }

    @Override
    public UserDto toDto(TbUsers entity, Timestamp expiredDate, List<String> authorities) {
        if ( entity == null && expiredDate == null && authorities == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

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
