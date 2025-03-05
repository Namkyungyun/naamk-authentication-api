package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.AuthService;
import kr.co.naamk.naamkauthenticationapi.web.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object create( HttpServletRequest request, @RequestBody UserDto.CreateRequest dto ) {

        UserDto result = userService.createUser( dto );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }


    @PutMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object update( HttpServletRequest request, @RequestBody UserDto.UpdateRequest dto ) {

        UserDto result = userService.updateUser( dto );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @PutMapping(value="/user-access", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object updateUserAccess(HttpServletRequest request, @RequestBody UserDto.AccessRequest dto) {

        UserDto.AccessResponse result = userService.updateUserAccess( dto );
        authService.logout( result.getUsername() );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

}
