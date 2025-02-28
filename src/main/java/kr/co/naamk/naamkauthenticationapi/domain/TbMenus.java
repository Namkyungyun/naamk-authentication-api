package kr.co.naamk.naamkauthenticationapi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.naamk.naamkauthenticationapi.domain.audit.AuditEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "menus", schema = "admin")
public class TbMenus extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "menus_id_gen")
    @SequenceGenerator(name = "menus_id_gen", sequenceName = "menus_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 10)
    @NotNull
    @Column(name = "code", unique = true, nullable = false, length = 10)
    @Comment( "메뉴 코드" )
    private String code;

    @Size(max = 50)
    @NotNull
    @Column(name = "name", nullable = false, length = 10)
    @Comment( "메뉴명" )
    private String name;

    @Size(max = 50)
    @Column(name = "desc", length = 50)
    @Comment( "메뉴 설명" )
    private String desc;


    @Column(name="parent_id")
    @Comment("상위 메뉴 ID")
    private Integer parentId;

    @NotNull
    @Column(name="order", nullable = false)
    @Comment("메뉴 정렬 순서 번호")
    private Integer order;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    @Comment( "사용 여부" )
    private Boolean isActive = false;

}