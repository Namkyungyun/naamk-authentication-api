package kr.co.naamk.naamkauthenticationapi.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Perms {
    CREATE("C", "생성"),
    READ("R", "조회"),
    UPDATE("U", "수정"),
    DELETE("D", "삭제"),
    ;

    private final String code;
    private final String name;
}
