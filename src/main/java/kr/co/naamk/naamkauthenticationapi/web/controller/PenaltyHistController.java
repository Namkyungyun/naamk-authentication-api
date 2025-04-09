package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.domain.common.TbUsers;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.model.RedisNotificationEntity;
import kr.co.naamk.naamkauthenticationapi.redis.type.NotificationType;
import kr.co.naamk.naamkauthenticationapi.web.dto.PenaltyHistDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.PenaltyHistService;
import kr.co.naamk.naamkauthenticationapi.redis.service.RedisNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/v1/penalty-hist")
@RequiredArgsConstructor
public class PenaltyHistController {

    private final PenaltyHistService penaltyHistService;
    private final RedisNotificationService redisNotificationService;

    @GetMapping(value = "/{type}/{linkedId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getPenaltyHistByLinkedIdAndType( HttpServletRequest request,
                                              @PathVariable(value="type") String type,
                                              @PathVariable(value = "linkedId") Long linkedId,
                                              Pageable pageable
                                              ) {
        // user data 저장
        Page< PenaltyHistDto > result = penaltyHistService.findUserPenaltyHistListByUserId( linkedId, type, pageable);

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    @PostMapping(value="/{type}/{linkedId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createPenaltyHist( HttpServletRequest request,
                                         @PathVariable(value="type") String type,
                                         @PathVariable(value = "linkedId") Long linkedId,
                                         @RequestBody PenaltyHistDto.CreateRequest dto) {
        // penalty 처리
        PenaltyHistDto.CreateResponse result = penaltyHistService.savePenalty( linkedId, type, dto );

        // 알림 전송
        Boolean isBlock = !dto.getIsActive(); // 제제 여부  [ isActive:true = 정상 | isActive:false = 차단 ]
        redisNotificationService.saveNotificationPenalty( result.getUsername(), NotificationType.fromTypeName( type ), isBlock, linkedId );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

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
