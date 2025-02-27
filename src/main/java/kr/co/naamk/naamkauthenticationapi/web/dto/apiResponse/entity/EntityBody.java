package kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityBody {

    private Object entity;

    public void setEntity(final Object obj) {
        if(Objects.isNull(obj)) {
            this.entity = StringUtils.EMPTY;
        } else {
            this.entity = obj;
        }

    }
}
