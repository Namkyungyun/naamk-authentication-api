package kr.co.naamk.naamkauthenticationapi.mapstruct;

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
    public TbUsers toEntity(UserDto.CreateRequest dto) {
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
}
