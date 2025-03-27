package kr.co.naamk.naamkauthenticationapi.domain.community;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.naamk.naamkauthenticationapi.domain.audit.AuditEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "penalty_hist", schema = "community")
public class TbPenaltyHist extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "penalty_hist_id_gen")
    @SequenceGenerator(name = "penalty_hist_id_gen", sequenceName = "penalty_hist_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 5)
    @NotNull
    @Column(name = "type", nullable = false, length = 5)
    private String type;

    @NotNull
    @Column(name = "linked_id", nullable = false)
    private Long linkedId;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @NotNull
    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

}