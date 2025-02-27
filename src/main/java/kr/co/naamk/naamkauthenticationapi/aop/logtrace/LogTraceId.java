package kr.co.naamk.naamkauthenticationapi.aop.logtrace;

import lombok.Getter;

import java.util.UUID;

@Getter
public class LogTraceId {
    private final String id;
    private final int level;


    public LogTraceId() {
        this.id = createId();
        this.level = 0;
    }

    private LogTraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public LogTraceId createNextId() {
        return new LogTraceId(this.id, this.level + 1);
    }

    public LogTraceId createPreviousId() {
        return new LogTraceId(this.id, this.level - 1);
    }

    public boolean isFirstLevel() {
        return this.level == 0;
    }
}
