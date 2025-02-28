package kr.co.naamk.naamkauthenticationapi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "role_menus", schema = "admin")
public class TbRoleMenus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_menus_id_gen")
    @SequenceGenerator(name = "role_menus_id_gen", sequenceName = "role_menus_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    @Comment( "역할ID" )
    private TbRoles role;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    @Comment( "메뉴ID" )
    private TbMenus menu;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    @Comment( "해당 역할의 메뉴 활성 여부" )
    private Boolean isActive;

}