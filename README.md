# Aprendiendo Spring Security

Proyecto desarrollado para aprender los fundamentos de Spring Security 6 utilizando Spring Boot 3 y Java 21.

## Tecnologías utilizadas

* Java 21
* Spring Boot 3
* Spring Security 6
* Spring Data JPA
* MySQL
* Maven
* Lombok

## Objetivos del proyecto

* Configurar Spring Security mediante SecurityFilterChain.
* Implementar autenticación con AuthenticationManager.
* Configurar AuthenticationProvider y UserDetailsService.
* Trabajar con Roles y Permisos.
* Modelar usuarios, roles y permisos utilizando JPA.
* Comprender el funcionamiento de la autorización mediante Authorities.
* Preparar la base para una futura implementación con JWT.

## Estructura de Seguridad

### Roles

* ADMIN
* USER
* INVITED
* DEVELOPER

### Permisos

* CREATE
* READ
* UPDATE
* DELETE
* REFACTOR

## Modelo de Datos

### UserEntity

Representa los usuarios del sistema.

### RoleEntity

Representa los roles asignables a los usuarios.

### PermissionEntity

Representa los permisos específicos asociados a cada rol.

### Relaciones

* Un usuario puede tener múltiples roles.
* Un rol puede pertenecer a múltiples usuarios.
* Un rol puede tener múltiples permisos.
* Un permiso puede pertenecer a múltiples roles.

## Endpoints de prueba

### Público

GET /auth/hello

Acceso permitido para cualquier usuario.

### Protegido

GET /auth/hello-secured

Requiere la autoridad READ.

### Protegido por clase

GET /auth/hello-secured2

Actualmente bloqueado por la anotación:

@PreAuthorize("denyAll()")

## Conceptos practicados

* SecurityFilterChain
* AuthenticationManager
* AuthenticationProvider
* UserDetailsService
* InMemoryUserDetailsManager
* Roles
* Authorities
* @PreAuthorize
* JPA Relationships
* ManyToMany
* CommandLineRunner
* SessionCreationPolicy.STATELESS


