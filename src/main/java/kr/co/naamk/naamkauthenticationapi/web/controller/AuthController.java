package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisRoleEntity;
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
    public Object login( HttpServletRequest request, @RequestBody AuthDto.LoginRequest dto ) {
        AuthDto.LoginResponse result = authService.login( dto );
        authService.updateRoleAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object logout( HttpServletRequest request, @RequestBody AuthDto.LoginRequest dto ) {
        // TODO 로그아웃 서비스 로직 연결

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    @GetMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getAuthorities( HttpServletRequest request ) {
        Iterable< RedisRoleEntity > result = authService.getRedisRoleAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();

    }

    @GetMapping(value = "/auth/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object authRefresh( HttpServletRequest request ) {
        authService.updateRoleAuthorities();
        Iterable< RedisRoleEntity > result = authService.getRedisRoleAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();

    }
}
