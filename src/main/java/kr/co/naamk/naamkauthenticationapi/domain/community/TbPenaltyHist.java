package kr.co.naamk.naamkauthenticationapi.domain.community;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.naamk.naamkauthenticationapi.domain.audit.AuditEntity;
import kr.co.naamk.naamkauthenticationapi.domain.audit.AuditOnlyCreateEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "penalty_hist", schema = "community")
public class TbPenaltyHist extends AuditOnlyCreateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "penalty_hist_id_gen")
    @SequenceGenerator(name = "penalty_hist_id_gen", sequenceName = "community.penalty_hist_id_seq", allocationSize = 1)
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