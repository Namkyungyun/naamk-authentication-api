package kr.co.naamk.naamkauthenticationapi.exception;

import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ServiceException extends RuntimeException {

    private int errCode;
    private String errDesc;
    private String errorDetailDesc;

    public ServiceException( ServiceMessageType type) {
        this.errCode = type.getCode();
        this.errDesc = type.name();
        this.errorDetailDesc = type.getServiceMessage();
    }

    public ServiceException( ServiceMessageType type, String errDetailDesc) {
        this.errCode = type.getCode();
        this.errDesc = type.name();
        this.errorDetailDesc = errDetailDesc;
    }

    public ServiceException( ServiceMessageType type, String errDesc, String errDetailDesc) {
        this.errCode = type.getCode();
        this.errDesc = errDesc;
        this.errorDetailDesc = errDetailDesc;
    }

}
