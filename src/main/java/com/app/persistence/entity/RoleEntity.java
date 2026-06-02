package com.app.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class RoleEntity {

    //UN ROL PUEDE TENER MUCHOS PERMISOS
    // Y MUCHOS PERMISOS PUEDEN ESTAR ASIGNADOS A MUCHOS ROLES
    // EJ EL ADMIN TIENE CREATE DELETE ETC

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name")
    //Se va a manejar como string
    @Enumerated(EnumType.STRING)
    private RoleEnum roleEnum;


    //El sirve para que si yo guardo un rol en la BD
    //Tambien va a agregar los permisos que temga asociados
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<PermissionEntity> permissionsList = new HashSet<>();
}
