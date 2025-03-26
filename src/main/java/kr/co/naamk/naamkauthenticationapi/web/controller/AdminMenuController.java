package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminMenuDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.AdminAuthService;
import kr.co.naamk.naamkauthenticationapi.web.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class AdminMenuController {

    private final AdminMenuService adminMenuService;
    private final AdminAuthService adminAuthService;

    @PostMapping(value="/menu", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createMenu( HttpServletRequest request, @RequestBody AdminMenuDto.CreateRequest dto ) {

        AdminMenuDto result = adminMenuService.createMenu(dto);
        adminAuthService.refreshAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }



    @PutMapping(value="/menu", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object updateMenu( HttpServletRequest request,
                              @RequestBody AdminMenuDto.UpdateRequest dto) {

        AdminMenuDto result = adminMenuService.updateMenu(dto);
        adminAuthService.refreshAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }



    @DeleteMapping(value="/menu/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object deleteMenu( HttpServletRequest request, @PathVariable Integer id) {

        Map<String, Boolean> result = adminMenuService.deleteMenu(id);
        adminAuthService.refreshAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

}
