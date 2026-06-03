package com.app;

import com.app.persistence.entity.PermissionEntity;
import com.app.persistence.entity.RoleEntity;
import com.app.persistence.entity.RoleEnum;
import com.app.persistence.entity.UserEntity;
import com.app.persistence.entity.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

//Sistema autenticacion roles y permisos
@SpringBootApplication
public class SpringSecurityAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAppApplication.class, args);
	}

	//Este metodo se ejecuta a penas levante la aplicacion
	@Bean
	CommandLineRunner init(UserRepository userRepository) {
		return args -> {

			/* crear permisos */
			PermissionEntity createPermission = PermissionEntity.builder()
					.name ("CREATE")
					.build();

			PermissionEntity readPermission = PermissionEntity.builder()
					.name("READ")
					.build();

			PermissionEntity updatePermission = PermissionEntity.builder()
					.name ("UPDATE")
					.build();

			PermissionEntity deletePermission = PermissionEntity.builder()
					.name("DELETE")
					.build();

			PermissionEntity refactorPermission = PermissionEntity.builder()
					.name ("REFACTOR")
					.build();

			/* crear roles*/

			RoleEntity roleAdmin = RoleEntity.builder()
					.roleEnum(RoleEnum.ADMIN)
					.permissionsList(Set.of(createPermission, readPermission, updatePermission, deletePermission))
					.build();

			RoleEntity roleUser = RoleEntity.builder()
					.roleEnum(RoleEnum.USER)
					.permissionsList(Set.of(createPermission, readPermission))
					.build();

			RoleEntity roleInvited = RoleEntity.builder()
					.roleEnum(RoleEnum.INVITED)
					.permissionsList(Set.of(readPermission))
					.build();

			RoleEntity roleDeveloper = RoleEntity.builder()
					.roleEnum(RoleEnum.DEVELOPER)
					.permissionsList(Set.of(createPermission, readPermission, updatePermission, deletePermission, refactorPermission))
					.build();


			/* Crear usuarios*/

			UserEntity userIgnacio = UserEntity.builder()
					.username("ignacio")
					.password("$2a$10$y9rhjlCdZiAe2G0l71Qt..nrVoSTNG.SHRgwLb25ICS4Xre4tLbfe")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleAdmin))
					.build();

			UserEntity userDaniel = UserEntity.builder()
					.username("daniel")
					.password("$2a$10$y9rhjlCdZiAe2G0l71Qt..nrVoSTNG.SHRgwLb25ICS4Xre4tLbfe")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleUser))
					.build();


			UserEntity userAndrea = UserEntity.builder()
					.username("andrea")
					.password("$2a$10$y9rhjlCdZiAe2G0l71Qt..nrVoSTNG.SHRgwLb25ICS4Xre4tLbfe")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleInvited))
					.build();

			UserEntity userLucila = UserEntity.builder()
					.username("lucila")
					.password("$2a$10$y9rhjlCdZiAe2G0l71Qt..nrVoSTNG.SHRgwLb25ICS4Xre4tLbfe")
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(roleDeveloper))
					.build();


			userRepository.saveAll(List.of(userIgnacio, userDaniel, userAndrea, userLucila));
		};
	}

}
