package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.PostReportDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserReportDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.PostReportService;
import kr.co.naamk.naamkauthenticationapi.web.service.UserReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post-reports")
public class PostReportController {

    private final PostReportService postReportService;

    @GetMapping(value="/search-options", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getSearch(HttpServletRequest request) {
        PostReportDto.SearchOption result = postReportService.getSearch();

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @PostMapping(value = "/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUsers( HttpServletRequest request, @RequestBody PostReportDto.SearchRequest dto, Pageable pageable ) {
        Page< PostReportDto.ListResponse > result = postReportService.findAllPostReport(dto, pageable);

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @GetMapping(value="/posts/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUserById(HttpServletRequest request, @PathVariable(value="postId") Long postId) {

        PostReportDto.DetailResponse result = postReportService.findLatestPostReport( postId );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }



    @PostMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createReport(HttpServletRequest request, @RequestBody PostReportDto.CreateRequest dto){
        Object result = postReportService.createPostReport( dto );
        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @GetMapping(value="/posts/{postId}/report-hist", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUserReportHistById(HttpServletRequest request,
                                        @PathVariable(value="postId") Long postId,
                                        Pageable pageable) {

        Map<String, Object> result = postReportService.findPostReportHist( postId , pageable);

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

}
