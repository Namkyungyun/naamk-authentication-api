package kr.co.naamk.naamkauthenticationapi.aop;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.naamk.naamkauthenticationapi.exception.ExcludeFromAdvice;
import kr.co.naamk.naamkauthenticationapi.web.dto.apiResponse.APIResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

@Slf4j
@Aspect
@Component
public class ControllerLogAspect {

    final String CALLING_ARROW = ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";
    final String END_ARROW = "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";
    private final String SPACE = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";

    @Pointcut("execution(* kr.co.naamk.naamkauthenticationapi.web.controller..*(..))")
    public void allController() {}

    @Around("allController()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        StringBuilder commonStr = new StringBuilder();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        APIResponse result = null;

        if(requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

            // ipAddress
            String ipAddress = getIpAddress(request);
            commonStr.append(SPACE).append("* ipAddress : ").append(ipAddress).append("\n");
            
            // httpMethod & requestAPI uri
            String httpMethod = request.getMethod();
            String requestURI = request.getRequestURI();
            commonStr.append(SPACE).append("* requestURI : ").append(httpMethod).append(" ").append(requestURI).append("\n");
        }

        try {
            /** 비즈니스 로직으로 연결되기 전 inbound 정보 log 남기기
             * 로그 규격
             *  [request API] {httpMethod} {api uri}
             *  [request ARGS] {request args}
             * */
            String beforeMessage = commonStr + getRequestInfo(joinPoint);
            log.info("{} CALLING \n{}", CALLING_ARROW, beforeMessage);


            // 커스텀 어노테이션 확인
            boolean excludeFromAdvice = isExcludeFromAdvice(joinPoint);

            if(!excludeFromAdvice) {
                result = (APIResponse) joinPoint.proceed();
            } else {
                joinPoint.proceed();
            }

            /** 비즈니스 로직 연결 이후 성공 케이스에 대한 log 남기기
             * 로그 규격
             *  [request IpAddress] {ipAddress}
             *  [request API] {httpMethod} {api uri}
             *  [response MSG] {response msg}
             * */
            String afterMessage = commonStr + getResponseMessage(result);
            log.info("{} COMPLETED {}\n{}", END_ARROW, getNow(), afterMessage);


            return result;

        } catch(Exception e) {
            /** 비즈니스 로직 연결 이후 예외 케이스에 대한 log 남기기
             * 로그 규격
             *  [request IpAddress] {ipAddress}
             *  [request API] {httpMethod} {api uri}
             *  [response MSG] {response msg}
             * */
            String exMessage = commonStr + getExceptionMessage(result, e);

            log.error("{} EXCEPTION \n{}", END_ARROW, exMessage);

            throw e;
        }
    }

    @AfterThrowing(pointcut = "allController()", throwing = "ex")
    public void logExceptionAndRethrow(Exception ex) throws Exception {
        throw ex; // 예외를 다시 던짐 -> APIExceptionHandler 가도록
    }


    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();

            if (isIPv4(request.getRemoteAddr())) {
                ipAddress = "Client IP (IPv4), " + request.getRemoteAddr();
            } else if (isIPv6(ipAddress)) {
                ipAddress = "Client IP (IPv6), " + request.getRemoteAddr();
            } else {
                ipAddress = "Client IP (Unknown format): " + request.getRemoteAddr();
            }
        }

        return ipAddress;
    }


    // IP 주소가 IPv4인지 확인
    public static boolean isIPv4(String ip) {
        String IPV4_PATTERN = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$";
        return Pattern.compile(IPV4_PATTERN).matcher(ip).matches();
    }

    // IP 주소가 IPv6인지 확인
    public static boolean isIPv6(String ip) {
        String IPV6_PATTERN =  "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3,3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3,3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))$";
        return Pattern.compile(IPV6_PATTERN).matcher(ip).matches();
    }


    private String getNow() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return dateFormat.format(System.currentTimeMillis());
    }

    private String getRequestInfo(ProceedingJoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();

        // request time
        sb.append(SPACE).append("* date : ").append(getNow()).append("\n");

        // request arguments
        Object[] args = joinPoint.getArgs();
        int argsCount = args.length;

        if(argsCount > 0) {
            sb.append(SPACE).append("* arguments : ");

            for(Object o : joinPoint.getArgs()) {
                if(o == null) {
                    continue;
                }

                sb.append("<").append(o.getClass().getSimpleName()).append("> ");
                sb.append(o.getClass().getSimpleName());
                sb.append(", ");
            }
            sb.delete(sb.length() - 2, sb.length()); // 마지막 , 지우기
        }

        return sb.toString();
    }

    private String getResponseMessage(APIResponse result) {
        if(result == null)  return "";

        StringBuilder sb = new StringBuilder();

        // status code
        Integer statusCode = result.getHeader().getResultCode();
        sb.append(SPACE).append("* result : ").append("\n");
        sb.append(SPACE).append("\t- return code : ").append(statusCode).append("\n");

        // body
        Object body = result.getBody().getEntity();

        String name = "";
        if(body != null) {
            name = body.getClass().getName();
        }

        sb.append(SPACE).append("\t- return val : ").append(name);

        return sb.toString();
    }

    private String getExceptionMessage(APIResponse result, Exception e) {
        StringBuilder sb = new StringBuilder();

        // status code
        if(result != null) {
            Integer statusCode = result.getHeader().getResultCode();
            sb.append(SPACE).append("* result : ").append("\n");
            sb.append(SPACE).append("\t- return code : ").append(statusCode).append("\n");
        }

        // error message
        String message = e.getMessage() != null ? e.getMessage() : e.toString();
        String exceptionType = e.getClass().getSimpleName();
        sb.append(SPACE).append("\t- error type : ").append(exceptionType).append("\n");
        sb.append(SPACE).append("\t- error msg : ").append(message).append("\n");

        return sb.toString();
    }

    private boolean isExcludeFromAdvice(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        return joinPoint.getTarget().getClass()
                .getMethod(joinPoint.getSignature().getName(),
                        ((MethodSignature) joinPoint.getSignature()).getParameterTypes())
                .isAnnotationPresent( ExcludeFromAdvice.class);
    }
}
