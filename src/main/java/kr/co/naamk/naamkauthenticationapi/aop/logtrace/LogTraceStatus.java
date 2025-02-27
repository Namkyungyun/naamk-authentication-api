package kr.co.naamk.naamkauthenticationapi.aop.logtrace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LogTraceStatus {
    private final LogTraceId traceId;
    private final Long startTimeMs;
    private final String message;
}
