package kr.co.naamk.naamkauthenticationapi.web.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.domain.admin.TbAdminUserRoles;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.admin.AdminUserDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.admin.AdminAuthService;
import kr.co.naamk.naamkauthenticationapi.web.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final AdminAuthService adminAuthService;

    @PostMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object create( HttpServletRequest request, @RequestBody AdminUserDto.CreateRequest dto ) {

        AdminUserDto result = adminUserService.createUser( dto );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }


    @PutMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object update( HttpServletRequest request, @RequestBody AdminUserDto.UpdateRequest dto ) {

        AdminUserDto result = adminUserService.updateUser( dto );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @PutMapping(value="/admin-access", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object updateUserAccess(HttpServletRequest request, @RequestBody AdminUserDto.AccessRequest dto) {

        AdminUserDto.AccessResponse result = adminUserService.updateUserAccess( dto );
        adminAuthService.logout( result.getUsername() );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    @GetMapping(value="/admin/{id}/delegate-access", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object delegateInactiveRoleAccess(HttpServletRequest request, @PathVariable(value="id") Integer id) {

        List< TbAdminUserRoles > result = adminUserService.delegateAllInactiveUserAccess( id );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    @PostMapping(value="/admin-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object updatePassword(HttpServletRequest request, @RequestBody AdminUserDto.PasswordRequest dto) {

        AdminUserDto.PasswordResponse result = adminUserService.updatePassword( dto );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }



}
