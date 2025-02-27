package kr.co.naamk.naamkauthenticationapi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_terms", schema = "public")
public class TbUserTerm {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_terms_id_gen")
    @SequenceGenerator(name = "user_terms_id_gen", sequenceName = "user_terms_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "term_id", nullable = false)
    private Integer termId;

    @NotNull
    @Column(name = "term_val", nullable = false)
    private Boolean termVal = false;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Column(name = "created_at")
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}