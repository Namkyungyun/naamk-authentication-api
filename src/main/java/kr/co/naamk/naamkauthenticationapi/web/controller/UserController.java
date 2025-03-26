package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.AdminUserDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-management")
public class UserController {

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object create( HttpServletRequest request, @RequestBody AdminUserDto.CreateRequest dto ) {


        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

}
