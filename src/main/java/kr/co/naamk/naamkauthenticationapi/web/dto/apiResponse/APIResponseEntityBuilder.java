package kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse;


import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;

import java.util.Objects;

public class APIResponseEntityBuilder extends APIResponseBuilder<APIResponseBuilder<APIResponse>>{
    private APIResponse entity;

    public APIResponseEntityBuilder() {
        super();
        entity = new APIResponse();
    }

    /** 사용처 :: 응답값을 만들어야 하는 모든 곳들 (Controller, APIExceptionHandler(ControllerAdvice))
     * */
    public static APIResponseEntityBuilder create() {
        return new APIResponseEntityBuilder();
    }

    /** ServiceMessageType으로 넘어가지 못한 파트
     * */
    public APIResponseEntityBuilder resultMessage(Integer code, String name) {
        return resultMessage(code , name, null);
    }

    public APIResponseEntityBuilder resultMessage(String detailMessage) {
        return resultMessage(null, null, detailMessage);
    }

    public APIResponseEntityBuilder resultMessage(Integer code, String name, String detailMessage) {
        checkResponseHeader(entity); // if entity is null, create entity.

        if(code != null) {
            entity.getHeader().setResultCode(code);
        }

        if(name != null) {
            entity.getHeader().setResultMessage(name);
        }

        if(detailMessage != null) {
            entity.getHeader().setDetailMessage(detailMessage);
        }

        return this;
    }

    /** ServiceMessageType으로 넘어간 파트
     * */
    public APIResponseEntityBuilder resultMessage( ServiceMessageType type) {
        checkResponseHeader(entity);
        entity.getHeader().setResultMessage(type);

        return this;
    }

    public APIResponseEntityBuilder resultMessage(ServiceMessageType type, String detailMessage) {
        checkResponseHeader(entity);
        entity.getHeader().setResultMessage(type, detailMessage);

        return this;
    }

    /** request 할때의 API path uri
     * */
    public APIResponseEntityBuilder service(HttpServletRequest request) {
        checkResponseHeader(entity);

        entity.getHeader().setActionUrl(request.getRequestURI());
        entity.getHeader().setActionMethod(request.getMethod());
        entity.getHeader().setResponseTime(System.currentTimeMillis()+ "");

        return this;
    }

    public APIResponseEntityBuilder entity(final Object obj) {
        checkResponseBody(entity);

        if(Objects.isNull(obj)) {
            return this;
        }

        entity.getBody().setEntity(obj);

        return this;
    }

    public APIResponse build() {
        checkResponseBody(entity);
        return this.entity;
    }

}
