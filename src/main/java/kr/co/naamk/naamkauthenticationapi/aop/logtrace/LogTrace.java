package kr.co.naamk.naamkauthenticationapi.aop.logtrace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EXCEPTION_PREFIX = "<X-";

    private final ThreadLocal<LogTraceId> traceIdHolder = new ThreadLocal<>();

    public LogTraceStatus begin(String message) {
        syncTraceId();
        LogTraceId traceId = traceIdHolder.get();
        Long startTimeMS = System.currentTimeMillis();

        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);

        return new LogTraceStatus(traceId, startTimeMS, message);
    }

    public void end(LogTraceStatus status) {
        complete(status, null);
    }

    public void exception(LogTraceStatus status, Exception exception) {
        complete(status, exception);

    }

    private void syncTraceId() {
        LogTraceId traceId = traceIdHolder.get();

        if(traceId == null) {
            traceIdHolder.set(new LogTraceId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    private String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < level; i++) {
            sb.append((i == level-1) ? "|" + prefix : "|  ");
        }

        return sb.toString();
    }

    private void complete(LogTraceStatus status, Exception exception) {
        Long endTimeMs = System.currentTimeMillis();
        Long resultTimeMs = endTimeMs - status.getStartTimeMs();

        LogTraceId traceId = status.getTraceId();

        if(exception == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs);
        } else {
            log.error("[{}] {}{} time={}ms ex={}", traceId.getId(), addSpace(EXCEPTION_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs, exception.toString());
        }

        releaseTraceId();
    }

    private void releaseTraceId() {
        LogTraceId traceId = traceIdHolder.get();

        if(traceId.isFirstLevel()) {
            traceIdHolder.remove();
        } else {
            traceIdHolder.set(traceId.createPreviousId());
        }
    }

}
