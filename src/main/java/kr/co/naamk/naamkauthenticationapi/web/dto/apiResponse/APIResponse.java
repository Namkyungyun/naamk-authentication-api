package kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse;

import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.entity.EntityBody;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.entity.EntityHeader;
import lombok.Data;

@Data
public class APIResponse {
    protected EntityHeader header;
    protected EntityBody body;
}
