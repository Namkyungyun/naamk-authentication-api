package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.domain.common.TbUsers;
import kr.co.naamk.naamkauthenticationapi.domain.type.PenaltyType;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisNotificationEntity;
import kr.co.naamk.naamkauthenticationapi.redis.type.NotificationType;
import kr.co.naamk.naamkauthenticationapi.web.dto.PenaltyHistDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.PenaltyHistService;
import kr.co.naamk.naamkauthenticationapi.web.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/v1/penalty-hist")
@RequiredArgsConstructor
public class PenaltyHistController {

    private final PenaltyHistService penaltyHistService;
    private final RedisService redisService;

    @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUserPenaltyHistByUserId( HttpServletRequest request, @PathVariable(value = "userId") long userId ) {
        // user 조회
        TbUsers user = penaltyHistService.getUserById( userId );

        // user data 저장
        List< PenaltyHistDto > result = penaltyHistService.findHistListByUserIdAndType( user.getId(), PenaltyType.user.name() );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    @PostMapping(value="/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createUserPenaltyHist( HttpServletRequest request,
                                         @PathVariable(value = "userId") long userId,
                                         @RequestBody PenaltyHistDto.CreateRequest dto) {
        // user 조회
        TbUsers user = penaltyHistService.getUserById( userId );

        PenaltyHistDto result = penaltyHistService.saveUserPenalty( user.getId(), dto );

        // 알림 전송
        Long linkedId = user.getId(); // 해당 사용자의 id
        Boolean isBlock = !dto.getIsActive(); // 제제 여부  [ isActive:true = 정상 | isActive:false = 차단 ]
        redisService.saveNotificationPenalty( user.getUsername(), NotificationType.user, isBlock, linkedId );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    @GetMapping(value="/notifications/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getPenaltyNotifications( HttpServletRequest request , @PathVariable("userId") Long userId) {
        // user 조회
        TbUsers user = penaltyHistService.getUserById( userId );
        // redis 조회
        List< RedisNotificationEntity > result = redisService.searchNotification( user.getUsername() );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();

    }






}
