package com.app.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permissions")

//Vamos a tener
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //No se puede repetir el nombre, no puede ser nulo y no se puede actualizar
    @Column(unique = true, nullable = false, updatable = false)
    private String name;

}


