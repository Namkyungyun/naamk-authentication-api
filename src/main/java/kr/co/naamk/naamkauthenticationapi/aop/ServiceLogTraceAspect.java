package kr.co.naamk.naamkauthenticationapi.aop;

import kr.co.naamk.naamkauthenticationapi.aop.logtrace.LogTrace;
import kr.co.naamk.naamkauthenticationapi.aop.logtrace.LogTraceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class ServiceLogTraceAspect {
    private final LogTrace logTrace;

    @Pointcut("execution(* kr.co.naamk.naamkauthenticationapi.web..*(..))")
    private void allAPI() {}

    @Pointcut("execution(* kr.co.naamk.naamkauthenticationapi.config.security..*(..))")
    private void securityConfig() {}


//    @Around("allAPI() || securityConfig()")
    @Around("allAPI()")
    public Object traceLog(ProceedingJoinPoint joinPoint) throws Throwable {

        LogTraceStatus status = null;

        try {
            String targetName = getTargetName(joinPoint);

            status = logTrace.begin(targetName);
            Object result = joinPoint.proceed();
            logTrace.end(status);

            return result;

        } catch(Exception e) {
            logTrace.exception(status, e);

            throw  e;
        }
    }


    private String getTargetName(ProceedingJoinPoint joinPoint) {
        Class<?>[] interfaces = joinPoint.getTarget().getClass().getInterfaces();
        String targetName = joinPoint.getSignature().toShortString();

        // CrudRepository 하위 인터페이스를 찾기
        for (Class<?> iface : interfaces) {
            if (org.springframework.data.repository.CrudRepository.class.isAssignableFrom(iface)) {
                targetName = iface.getSimpleName() + "." + joinPoint.getSignature().getName();;
                break;
            }
        }

        return targetName;

    }
}
