package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminRoleDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.AdminAuthService;
import kr.co.naamk.naamkauthenticationapi.web.service.AdminRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleService adminRoleService;
    private final AdminAuthService adminAuthService;

    @PostMapping(value="/role", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createRole( HttpServletRequest request, @RequestBody AdminRoleDto.CreateRequest dto ) {

        AdminRoleDto result = adminRoleService.createRole( dto );
        adminAuthService.refreshAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    @PutMapping(value="/role")
    public Object updateRole(HttpServletRequest request, @RequestBody AdminRoleDto.UpdateRequest dto ) {

        AdminRoleDto result = adminRoleService.updateRole( dto );
        adminAuthService.refreshAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    /// 역할에 따른 Access 수정 (perms, menus)
    @PutMapping(value="/role-access", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object updateRoleAccess( HttpServletRequest request, @RequestBody AdminRoleDto.AccessRequest dto ) {

        AdminRoleDto.AccessResponse result = adminRoleService.updateRoleAccess( dto );
        adminAuthService.refreshAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

}
