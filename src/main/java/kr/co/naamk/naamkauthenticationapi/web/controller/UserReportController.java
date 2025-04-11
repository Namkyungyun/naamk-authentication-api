package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserReportDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.UserReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-reports")
public class UserReportController {

    private final UserReportService userReportService;

    @GetMapping(value = "/search-options", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getSearch( HttpServletRequest request ) {
        UserReportDto.SearchOption result = userReportService.getSearch();

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @PostMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUsers( HttpServletRequest request, @RequestBody UserReportDto.SearchRequest dto, Pageable pageable ) {
        Page< UserReportDto > result = userReportService.findAllUserReport( dto, pageable );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }


    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUserById( HttpServletRequest request, @PathVariable(value = "userId") Long userId ) {

        UserReportDto.UserDetailResponse result = userReportService.findLatestUserReport( userId );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @GetMapping(value = "/{userId}/report-hist", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUserReportHistById( HttpServletRequest request,
                                         @PathVariable(value = "userId") Long userId,
                                         Pageable pageable ) {

        Map< String, Object > result = userReportService.findUserReportHist( userId, pageable );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }


    /// test용
    @PostMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createReport( HttpServletRequest request, @RequestBody UserReportDto.CreateRequest dto ) {
        Object result = userReportService.createUserReport( dto );
        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

}
