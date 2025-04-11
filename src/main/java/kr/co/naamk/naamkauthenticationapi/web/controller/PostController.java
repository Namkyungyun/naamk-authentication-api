package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.domain.type.PenaltyType;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.redis.service.RedisNotificationService;
import kr.co.naamk.naamkauthenticationapi.redis.type.NotificationType;
import kr.co.naamk.naamkauthenticationapi.web.dto.PenaltyHistDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.PostDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.PenaltyHistService;
import kr.co.naamk.naamkauthenticationapi.web.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PenaltyHistService penaltyHistService;
    private final RedisNotificationService redisNotificationService;

    @GetMapping(value="/search-options", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getSearch(HttpServletRequest request) {
        PostDto.SearchOption result = postService.getSearch();

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @PostMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUsers( HttpServletRequest request, @RequestBody PostDto.SearchRequest dto, Pageable pageable ) {

        Page< PostDto > result = postService.findPostList(dto, pageable);

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @GetMapping(value="/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUserById(HttpServletRequest request, @PathVariable(value="postId") Long postId) {

        PostDto.PostDetailResponse result = postService.findPostById( postId );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @GetMapping(value = "/{linkedId}/penalty-hist", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getPenaltyHistByLinkedIdAndType( HttpServletRequest request,
                                                   @PathVariable(value = "linkedId") Long linkedId,
                                                   Pageable pageable
    ) {
        // user data 저장
        Page< PenaltyHistDto > result = penaltyHistService.findUserPenaltyHistListByUserId( linkedId, PenaltyType.post.getName(), pageable );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    @PostMapping(value = "/{linkedId}/penalty", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createPenaltyHist( HttpServletRequest request,
                                     @PathVariable(value = "linkedId") Long linkedId,
                                     @RequestBody PenaltyHistDto.CreateRequest dto ) {
        // penalty 처리
        PenaltyHistDto.CreateResponse result = penaltyHistService.savePenalty( linkedId, PenaltyType.post.getName(), dto );

        // 알림 전송
        Boolean isBlock = !dto.getIsActive(); // 제제 여부  [ isActive:true = 정상 | isActive:false = 차단 ]
        redisNotificationService.saveNotificationPenalty(
                result.getUsername(),
                NotificationType.fromTypeName( PenaltyType.user.getName() ),
                isBlock, linkedId );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

}
