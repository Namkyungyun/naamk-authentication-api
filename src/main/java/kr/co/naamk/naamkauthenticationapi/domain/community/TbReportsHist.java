package kr.co.naamk.naamkauthenticationapi.domain.community;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.naamk.naamkauthenticationapi.domain.audit.AuditOnlyDateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "reports_hist", schema = "community")
public class TbReportsHist extends AuditOnlyDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reports_hist_id_gen")
    @SequenceGenerator(name = "reports_hist_id_gen", sequenceName = "community.reports_hist_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    @Comment( "신고자" )
    private Long userId;

    @Size(max = 5)
    @NotNull
    @Column(name = "type", nullable = false, length = 5)
    @Comment( "enum post|user" )
    private String type;

    @NotNull
    @Column(name = "linked_id", nullable = false)
    @Comment( "post_id 혹은 user_id" )
    private Long linkedId;

    @ColumnDefault("true")
    @Column(name = "is_active")
    @Comment( "신고 여부 : true | 처리여부 : false" )
    private Boolean isActive;

}