package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.AuthDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object login( HttpServletRequest request, @RequestBody AuthDto.LoginRequest dto) {
        AuthDto.LoginResponse result = authService.login( dto );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object refresh( HttpServletRequest request, @RequestBody AuthDto.RefreshRequest dto) {
        AuthDto.RefreshResponse result = authService.refresh( dto );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }
}
