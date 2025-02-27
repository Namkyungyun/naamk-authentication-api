package kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse;


import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.entity.EntityBody;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.entity.EntityHeader;

public abstract class APIResponseBuilder<T> {

    protected void checkResponseHeader(APIResponse entity) {
        if(entity.getHeader() == null) {
            entity.setHeader(new EntityHeader());
        }
    }

    protected void checkResponseBody(APIResponse entity) {
        if(entity.getBody() == null) {
            entity.setBody(new EntityBody());
        }
    }
}
