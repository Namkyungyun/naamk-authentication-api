package kr.co.naamk.naamkauthenticationapi.domain.admin;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "user_roles", schema = "admin")
public class TbAdminUserRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin.user_roles_id_gen")
    @SequenceGenerator(name = "user_roles_id_gen", sequenceName = "admin.user_roles_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private TbAdminUsers user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private TbAdminRoles role;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

}