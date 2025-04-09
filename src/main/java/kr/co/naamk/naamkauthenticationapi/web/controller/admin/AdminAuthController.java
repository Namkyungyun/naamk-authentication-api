package kr.co.naamk.naamkauthenticationapi.web.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.dto.RedisTokenDto;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisRoleEntity;
import kr.co.naamk.naamkauthenticationapi.redis.service.RedisAccessService;
import kr.co.naamk.naamkauthenticationapi.web.dto.admin.AdminAuthDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.admin.AdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final RedisAccessService redisAccessService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object login( HttpServletRequest request, @RequestBody AdminAuthDto.LoginRequest dto ) {
        AdminAuthDto.LoginResponse result = adminAuthService.login( dto );
        redisAccessService.refreshAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @GetMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object logout( HttpServletRequest request) {
        adminAuthService.logout( null );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }


    @GetMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getAuthorities( HttpServletRequest request ) {
        Iterable< RedisRoleEntity > result = redisAccessService.getRedisRoleAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    @GetMapping(value = "/auth/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUserInfoByAccessToken( HttpServletRequest request ) {
        RedisTokenDto result = redisAccessService.findUserByAccessToken( request );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }


    @GetMapping(value = "/auth/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object refreshAuthorities( HttpServletRequest request ) {
        redisAccessService.refreshAuthorities();
        Iterable< RedisRoleEntity > result = redisAccessService.getRedisRoleAuthorities();

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();

    }
}
