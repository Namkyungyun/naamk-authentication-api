package kr.co.naamk.naamkauthenticationapi.domain.community;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.naamk.naamkauthenticationapi.domain.audit.AuditOnlyDateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "pop_user_hist", schema = "community")
public class TbPopUserHist extends AuditOnlyDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pop_user_hist_id_gen")
    @SequenceGenerator(name = "pop_user_hist_id_gen", sequenceName = "pop_user_hist_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Size(max = 10)
    @NotNull
    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @NotNull
    @Column(name = "pop_qty", nullable = false, precision = 10, scale = 2)
    private BigDecimal popQty;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

}