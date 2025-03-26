package kr.co.naamk.naamkauthenticationapi.utils;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class DateUtil {
    private final Integer EXPIRED_DAYS = 90;

    public Timestamp getExpiredAt(Timestamp changedAt) {
        LocalDateTime localDateTime = changedAt.toLocalDateTime();
        LocalDateTime newDateTime = localDateTime.plusDays( EXPIRED_DAYS );
        return Timestamp.valueOf( newDateTime );
    }

    public Timestamp getNow() {
        return Timestamp.valueOf( LocalDateTime.now() );
    }


}
