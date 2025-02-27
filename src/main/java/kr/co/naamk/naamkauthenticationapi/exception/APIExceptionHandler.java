package kr.co.naamk.naamkauthenticationapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.type.ServiceMessageType;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponseEntityBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class APIExceptionHandler {

    /** 'ko.co.naamk.web' 내의 ServiceException으로 throwing한 파트처리 (비즈니스 로직처리중 발생) **/
    @ExceptionHandler(ServiceException.class)
    public Object serviceException(HttpServletRequest request,  Exception e) {

        ServiceException se = (ServiceException) e;
        errorLog(e.getClass().getSimpleName() +" "+ se.getErrDesc(), se.getErrorDetailDesc());

        return APIResponseEntityBuilder.create().service(request)
                .resultMessage(se.getErrCode(), se.getErrDesc(), se.getErrorDetailDesc())
                .build();
    }


    @ExceptionHandler(TransactionSystemException.class)
    public Object transactionException(HttpServletRequest request,  TransactionSystemException e) {
        Throwable rootCause = e.getRootCause();
        errorLog(e.getClass().getSimpleName(), rootCause.getMessage());

        return APIResponseEntityBuilder.create().service(request)
                .resultMessage( ServiceMessageType.ERROR_SQL, e.getMessage())
                .build();
    }

    /** 데이터 접근 계층에서 발생하는 예외, (데이터 무결성 제약 조건을 위반하였을 때 발생.)
     * (1) 데이터 베이스 제약 조건 위반
     *      Primary Key 위반, Unique Constraint 위반, Foreign Key Constraints 위반, Not Null Constraints 위반
     * (2) SQL 실행 중 무결성 문제
     *      db의 스키마와 일치하지 않는 데이터 삽입.
     *      데이터 타입의 불일치.
     * (3) JPA 또는 Hibernate에서 매핑 오류
     *      잘못된 entity 매핑으로 데이터베이스 제약 조건을 위반할 경우.
     * */
    // DataIntegrityViolationException의 상위타입으로 처리
    @ExceptionHandler(DataAccessException.class)
    public Object dataAccessException(HttpServletRequest request,  Exception e) {
        errorLog(e.getClass().getSimpleName(), e.getMessage());

        return APIResponseEntityBuilder.create().service(request)
                .resultMessage( ServiceMessageType.ERROR_SQL, e.getMessage())
                .build();
    }


    /** Spring Framework에서 유효성 검사(Validate) 중 실패가 발생했을 때 던저지는 예외.
     * 주로 DTO 바인딩 과정에서 발생
     * (1) Controller 메서드의 파라미터 검증 실패
     *      @RequestBody, @ModelAttribute, @RequestParam과 함께 사용한 DTO유효성 검사 실패 시 발생.
     *      Hibernate Validator를 통해 정의된 제약 조건이 만족되지 않을 때 예외 발생.
     * (2) @Valid, @Validated 사용
     *      유효성 검사를 활성화하기 위해 DTO 파라미터에 @Valid, @Validated 어노테이션을 사용
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object methodArgumentNotValidException(HttpServletRequest request,  Exception e) {
        errorLog(e.getClass().getSimpleName(), e.getMessage());

        Map<String, String> errors = new HashMap<>();
        MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return APIResponseEntityBuilder.create().service(request)
                .resultMessage(ServiceMessageType.REQUEST_PARAM_ERROR)
                .entity(errors)
                .build();
    }

    /** 네트워크나 I/O 처리 중 발생. **/
    @ExceptionHandler(IOException.class)
    public Object ioException(HttpServletRequest request,  Exception e) {
        errorLog(e.getClass().getSimpleName(), e.getMessage());

        return APIResponseEntityBuilder.create().service(request)
                .resultMessage(ServiceMessageType.ERROR_IO)
                .build();
    }

    // 2. IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public Object illegalArgumentException(HttpServletRequest request, IllegalArgumentException e) {
        errorLog(e.getClass().getSimpleName(), e.getMessage());

        return APIResponseEntityBuilder.create().service(request)
                .resultMessage(ServiceMessageType.REQUEST_ENTITY_TYPE_ERROR)
                .build();
    }

    // 1. NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public Object nullPointerException(HttpServletRequest request, NullPointerException e) {
        errorLog(e.getClass().getSimpleName(), e.getMessage());

        return APIResponseEntityBuilder.create().service(request)
                .resultMessage(ServiceMessageType.ERROR_NULL_DATA)
                .build();
    }


    /** 기본 예외 처리*/
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object exception(HttpServletRequest request,  Exception e) {
//        errorLog(e.getClass().getSimpleName(), e.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred");
        error.put("details", e.getMessage());

        return APIResponseEntityBuilder.create().service(request)
                .resultMessage(ServiceMessageType.NOT_DEFINED_ERROR)
                .entity(error)
                .build();
    }


    private void errorLog(String exceptionClassName, String message) {
//        log.error( "============================== {} detailMessage!: {}", exceptionClassName, message);
    }
}
