package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminUserDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.AdminAuthService;
import kr.co.naamk.naamkauthenticationapi.web.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final AdminAuthService adminAuthService;

    @PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object create( HttpServletRequest request, @RequestBody AdminUserDto.CreateRequest dto ) {

        AdminUserDto result = adminUserService.createUser( dto );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }


    @PutMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object update( HttpServletRequest request, @RequestBody AdminUserDto.UpdateRequest dto ) {

        AdminUserDto result = adminUserService.updateUser( dto );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @PutMapping(value="/user-access", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object updateUserAccess(HttpServletRequest request, @RequestBody AdminUserDto.AccessRequest dto) {

        AdminUserDto.AccessResponse result = adminUserService.updateUserAccess( dto );
        adminAuthService.logout( result.getUsername() );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    @PostMapping(value="/user-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object updatePassword(HttpServletRequest request, @RequestBody AdminUserDto.PasswordRequest dto) {

        AdminUserDto.PasswordResponse result = adminUserService.updatePassword( dto );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }



}
