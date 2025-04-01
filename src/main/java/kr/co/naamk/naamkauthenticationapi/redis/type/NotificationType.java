package kr.co.naamk.naamkauthenticationapi.redis.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    post("post"),
    user("user"),
//    channel("channel"),
    ;

    private final String type;
}
