package kr.co.naamk.naamkauthenticationapi.config.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityException {

    /// 인증과 맞지 않음 (401 : UnAuthorize):  accessToken or refreshToken 불일치
    public void unAuthorization( HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Object responseDto = APIResponseEntityBuilder.create().service(request)
                .resultMessage( ServiceMessageType.SC_UNAUTHORIZED)
                .build();

        String responseBody = om.writeValueAsString(responseDto);
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(401);
        response.getWriter().println(responseBody);
    }


    /// 인증과 맞지 않음 (401 : UnAuthorize):  serviceMessageType값으로 내려주기
    public void unAuthorization( HttpServletRequest request, HttpServletResponse response, ServiceMessageType type) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Object responseDto = APIResponseEntityBuilder.create().service(request)
                .service( request )
                .resultMessage( type )
                .build();

        String responseBody = om.writeValueAsString(responseDto);
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(401);
        response.getWriter().println(responseBody);
    }




    /// 권한 없음 ( 403 : Forbidden )
    public void unAuthentication( HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Object responseDto = APIResponseEntityBuilder.create().service(request)
                .resultMessage(ServiceMessageType.SC_UNAUTHORIZED)
                .build();

        String responseBody = om.writeValueAsString(responseDto);
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(403);
        response.getWriter().println(responseBody);
    }


}
