package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.UserPenaltyDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.UserPenaltyService;
import kr.co.naamk.naamkauthenticationapi.web.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-penalty")
public class UserPenaltyController {

    private final UserPenaltyService userPenaltyService;

    @GetMapping(value="/search-options", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getSearch(HttpServletRequest request) {
        UserPenaltyDto.SearchOption result = userPenaltyService.getSearch();

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUsers( HttpServletRequest request, @RequestBody UserPenaltyDto.SearchRequest dto, Pageable pageable ) {
        Page< UserPenaltyDto > result = userPenaltyService.findUserList(dto, pageable);

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @PostMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object craeteReport(HttpServletRequest request, @RequestBody UserPenaltyDto.CreateRequest dto){
        Object result = userPenaltyService.createUserReport( dto );
        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }


    @GetMapping(value="/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUserById(HttpServletRequest request, @PathVariable(value="userId") Long userId) {

        UserPenaltyDto.UserDetailResponse result = userPenaltyService.findUserById( userId );

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

    @GetMapping(value="/users/{userId}/report-hist", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUserReportHistById(HttpServletRequest request,
                                        @PathVariable(value="userId") Long userId,
                                        Pageable pageable) {

        Page< UserPenaltyDto.ReportHistResponse > result = userPenaltyService.findUserReportHist( userId , pageable);

        return APIResponseEntityBuilder.create()
                .service( request )
                .resultMessage( ServiceMessageType.SUCCESS )
                .entity( result )
                .build();
    }

}
