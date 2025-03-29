package kr.co.naamk.naamkauthenticationapi.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.domain.type.PenaltyType;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.PenaltyHistDto;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import kr.co.naamk.naamkauthenticationapi.web.service.PenaltyHistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/v1/penalty-hist")
@RequiredArgsConstructor
public class PenaltyHistController {

    private final PenaltyHistService penaltyHistService;

    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUserPenaltyHistById( HttpServletRequest request, @PathVariable(value = "id") long id ) {
        List< PenaltyHistDto > result = penaltyHistService.getHistByIdAndType( id, PenaltyType.user.name() );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }

    @PostMapping(value="/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createUserPenaltyHist( HttpServletRequest request,
                                         @PathVariable(value = "id") long id,
                                         @RequestBody PenaltyHistDto.CreateRequest dto) {

        PenaltyHistDto result = penaltyHistService.saveUserPenalty( id, dto );

        return APIResponseEntityBuilder.create()
                .service( request )
                .entity( result )
                .resultMessage( ServiceMessageType.SUCCESS )
                .build();
    }






}
