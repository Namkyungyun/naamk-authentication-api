package kr.co.naamk.naamkauthenticationapi.domain.common;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "files", schema = "public")
public class TbFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_id_gen")
    @SequenceGenerator(name = "files_id_gen", sequenceName = "files_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 10)
    @NotNull
    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @NotNull
    @Column(name = "linked_id", nullable = false)
    private Long linkedId;

    @Size(max = 50)
    @NotNull
    @Column(name = "filename", nullable = false, length = 50)
    private String filename;

    @Size(max = 10)
    @Column(name = "file_type", length = 10)
    private String fileType;

    @Size(max = 20)
    @Column(name = "file_extension", length = 20)
    private String fileExtension;

    @Size(max = 255)
    @Column(name = "thumb_s_url")
    private String thumbSUrl;

    @Size(max = 255)
    @Column(name = "thumb_m_url")
    private String thumbMUrl;

    @Size(max = 255)
    @Column(name = "thumb_l_url")
    private String thumbLUrl;

    @Size(max = 255)
    @Column(name = "origin_img")
    private String originImg;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

}