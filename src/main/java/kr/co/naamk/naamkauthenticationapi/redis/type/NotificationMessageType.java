package kr.co.naamk.naamkauthenticationapi.redis.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationMessageType {
    post_penalty_ok( """
            [Notice] Account Restriction Cleared\n
            "Your account restrictions have been cleared. You can now post freely. Please follow our policies to avoid future restrictions.
            """ ), post_penalty_block( """
            [Notice] Account Restriction\n
            Your account has violated our policies. All past posts have been hidden, and new posts are restricted. Please check our policies for details.
            """ ), user_penalty_ok( """ 
            [Notice] Post Restriction Cleared\n
            Your hidden posts have been cleared. Please ensure future posts comply with our policies.
            """ ), user_penalty_block( """
            [Notice] Post Restriction\n
            Some of your posts violated our policies and have been hidden. Repeated violations may lead to further restrictions. Please review our policies.
            """ ),
    ;

    private final String message;
}
