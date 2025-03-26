package kr.co.naamk.naamkauthenticationapi.domain.community;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.naamk.naamkauthenticationapi.domain.audit.AuditOnlyDateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "posts", schema = "community")
public class Post extends AuditOnlyDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_id_gen")
    @SequenceGenerator(name = "posts_id_gen", sequenceName = "posts_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "channel_id")
    private Long channelId;

    @Size(max = 10)
    @NotNull
    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @Column(name = "content", length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "parent_id")
    private Long parentId;

    @ColumnDefault("false")
    @Column(name = "is_pin")
    private Boolean isPin;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;
}