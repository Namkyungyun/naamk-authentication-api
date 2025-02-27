package kr.co.naamk.naamkauthenticationapi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.naamk.naamkauthenticationapi.domain.audit.AuditEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "terms", schema = "admin")
public class TbTerms extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "terms_id_gen")
    @SequenceGenerator(name = "terms_id_gen", sequenceName = "terms_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'commonl'")
    @Column(name = "site_type", nullable = false, length = 50)
    private String siteType;

    @Size(max = 50)
    @NotNull
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Size(max = 10)
    @NotNull
    @ColumnDefault("'en'")
    @Column(name = "lang_cd", nullable = false, length = 10)
    private String langCd;

    @NotNull
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = false;

    @Size(max = 20)
    @NotNull
    @Column(name = "version", nullable = false, length = 20)
    private String version;

    @Size(max = 255)
    @NotNull
    @Column(name = "content_url", nullable = false)
    private String contentUrl;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

}