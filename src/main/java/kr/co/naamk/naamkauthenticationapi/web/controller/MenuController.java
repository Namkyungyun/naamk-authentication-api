package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.MenuDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.AuthService;
import kr.co.naamk.naamkauthenticationapi.web.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final AuthService authService;

    @PostMapping(value="/menu", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createMenu( HttpServletRequest request, @RequestBody MenuDto.CreateRequest dto ) {

        MenuDto result = menuService.createMenu(dto);
        authService.updateRoleAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }



    @PutMapping(value="/menu", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object updateMenu( HttpServletRequest request,
                              @RequestBody MenuDto.UpdateRequest dto) {

        MenuDto result = menuService.updateMenu(dto);
        authService.updateRoleAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }



    @DeleteMapping(value="/menu/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object deleteMenu( HttpServletRequest request, @PathVariable Integer id) {

        Map<String, Boolean> result = menuService.deleteMenu(id);
        authService.updateRoleAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

}
