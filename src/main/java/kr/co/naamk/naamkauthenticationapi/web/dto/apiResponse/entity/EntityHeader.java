package kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.entity;

import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import lombok.*;

@Data @Builder @ToString
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EntityHeader {

    private String responseTime;
    private String actionUrl;
    private String actionMethod;
    private Integer resultCode;
    private String resultMessage;
    private String detailMessage;

    public void setResultMessage( ServiceMessageType type) {
        this.resultCode = type.getCode();
        this.resultMessage = type.name();
        this.detailMessage = type.getServiceMessage();
    }

    public void setResultMessage(String typeName) {
        ServiceMessageType type = ServiceMessageType.valueOfString( typeName );

        if(type != null) {
            this.resultCode = type.getCode();
            this.resultMessage = type.name();
            this.detailMessage = type.getServiceMessage();
        }
        // null 이라면 위의 변수들 모두 null값을 담아 return
    }

    public void setResultMessage(ServiceMessageType type, String detailMessage) {
        if(type != null) {
            this.resultCode = type.getCode();
            this.resultMessage = type.name();
            this.detailMessage = type.getServiceMessage();
        }

        if(detailMessage != null) {
            this.detailMessage = detailMessage;
        }
    }
}
