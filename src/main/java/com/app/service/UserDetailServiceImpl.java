package com.app.service;

import com.app.controller.dto.AuthCreateUserRequest;
import com.app.controller.dto.AuthLoginRequest;
import com.app.controller.dto.AuthResponse;
import com.app.persistence.entity.RoleEntity;
import com.app.persistence.entity.RoleEnum;
import com.app.persistence.entity.UserEntity;
import com.app.persistence.entity.repository.RoleRepository;
import com.app.persistence.entity.repository.UserRepository;
import com.app.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Busca los usaurios en la base de datos
        UserEntity userEntity = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe"));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        //Agregamos los roles al alista de authorizacion
        userEntity.getRoles()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        //Recorriendo permiksos dentro de los roles
        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissionsList().stream())
                //A los permisos se les pasa el permiso directamente .name y listo
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));


        return new User(userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                //y le pasamos los permisos
                authorityList);
    }


    public AuthResponse loginUser (AuthLoginRequest authLoginRequest) {

        //Aca vamos a generar el token de ingreso
        //Recupero el usuario y el password
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(username, "User loged successfuly", accessToken, true);
        return authResponse;
    }

    public Authentication authenticate (String username, String password) {
        //Busca el usuario en la db

        UserDetails userDetails = this.loadUserByUsername(username);

        if(userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        //Si la contraseña no es la misma que en la base de datos
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public AuthResponse createUser(AuthCreateUserRequest authCreateUserRequest) {
        String username =  authCreateUserRequest.username();
        String password = authCreateUserRequest.password();
        //Se obtienen los roles que son asignados al usuario que se esta creando
        List<String> roleRequest = authCreateUserRequest.roleRequest().roleListName();
        //Hay que validar que los roles que envian son los mismos que estan en la tabla

        List<RoleEnum> roleEnums = roleRequest.stream()
                .map(RoleEnum::valueOf)
                .toList();

        //El set es para que no hgaya repetidos
        Set<RoleEntity> roleEntitySet = roleRepository.findByRoleEnumIn(roleEnums)
                .stream()
                .collect(Collectors.toSet());

        //Si el role esta vacio no crea el usuario
        if(roleEntitySet.isEmpty()) {
            throw new IllegalArgumentException("The roles specified does no exist.");
        }
        //si estatodo bien, crea el usuaario
        UserEntity userEntity = UserEntity.builder()
                .username(username)
                //Encriptamos la contraseña
                .password(passwordEncoder.encode(password))
                //ASIGNAMOS LOS ROLES
                .roles(roleEntitySet)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();

        //Lo guarda en la base de datos
        UserEntity userCreated = userRepository.save(userEntity);

        //le vamos a setear a la lista, lo que tenga el usuario
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

    //Seteamos roles
        userCreated.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

    //Seteamos permisos
        userCreated.getRoles()
                .stream()
                //El flat map es para ver que permisos tiene cada rol
                .flatMap(role -> role.getPermissionsList().stream())
                //Recorremos cada permiso y cada permiso agregamos a la lista
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        //creamos el objeto de autentiacion
        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(), userCreated.getPassword(), authorityList);

        //Creamos el token
        String accessToken = jwtUtils.createToken(authentication);

        //Damos la respuesta

        AuthResponse authResponse = new AuthResponse(userCreated.getUsername(), "User created successfuly", accessToken, true);

        return authResponse;
    }
}


