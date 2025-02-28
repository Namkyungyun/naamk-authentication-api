package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.RoleDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.AuthService;
import kr.co.naamk.naamkauthenticationapi.web.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final AuthService authService;

    @PostMapping(value="/role", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createRole( HttpServletRequest request, @RequestBody RoleDto.CreateRequest dto ) {

        RoleDto result = roleService.createRole( dto );
        authService.updateRoleAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    /// 역할에 따른 Access 수정 (perms, menus)
    @PutMapping(value="/role-auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object updateRolePerms(HttpServletRequest request, @RequestBody RoleDto.AuthorityRequest dto ) {

        Boolean result = roleService.updateRoleAuthorities( dto );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

}
