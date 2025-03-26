package kr.co.naamk.naamkauthenticationapi.domain.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.co.naamk.naamkauthenticationapi.domain.audit.AuditOnlyDateEntity;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "users", schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(name = "users_unique", columnNames = "name"),
                @UniqueConstraint(name = "users_username_key", columnNames = "username")
        })
public class TbUsers extends AuditOnlyDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "username", length = 100, nullable = false)
    private String username;

    @Column(name = "password", length = 150)
    private String password;

    @Column(name = "temp_password", length = 12)
    private String tempPassword;

    @NotNull
    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @NotNull
    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @NotNull
    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @NotNull
    @Column(name = "language", length = 4, nullable = false)
    private String language = "en";

    @NotNull
    @Column(name = "region", length = 4, nullable = false)
    private String region = "us";

    @Column(name = "recommand_name", length = 20)
    private String recommandName;

    @Column(name = "refresh_token", length = 100)
    private String refreshToken;

    @Column(name = "provider_id", length = 50)
    private String providerId;

    @Column(name = "provider", length = 20)
    private String provider;

    @Column(name = "profile_picture", length = 400)
    private String profilePicture;

    @Column(name = "role", length = 20)
    private String role;

    @Column(name = "intro", length = 500)
    private String intro;

    @Column(name = "pt_qty")
    private Integer ptQty = 0;

    @NotNull
    @Column(name = "is_two_fa", nullable = false)
    private Boolean isTwoFa = false;

    @Column(name = "two_fa_recovery", length = 12)
    private String twoFaRecovery;

    @NotNull
    @Column(name = "fail_cnt", nullable = false)
    private Integer failCnt = 0;

    @Column(name = "changed_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp changedAt;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

}
