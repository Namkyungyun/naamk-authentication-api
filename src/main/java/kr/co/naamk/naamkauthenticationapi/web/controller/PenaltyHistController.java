package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.domain.common.TbUsers;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisNotificationEntity;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.PenaltyHistService;
import kr.co.naamk.naamkauthenticationapi.redis.service.RedisNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/v1/penalty-hist")
@RequiredArgsConstructor
public class PenaltyHistController {

    private final PenaltyHistService penaltyHistService;
    private final RedisNotificationService redisNotificationService;


    /// 레디스 저장 확인용
    @GetMapping(value="/notifications/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getPenaltyNotifications( HttpServletRequest request , @PathVariable("userId") Long userId) {
        // user 조회
        TbUsers user = penaltyHistService.getUserById( userId );
        // redis 조회
        List< RedisNotificationEntity > result = redisNotificationService.searchNotification( user.getUsername() );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }



}
