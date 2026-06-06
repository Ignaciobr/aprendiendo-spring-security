package com.app.config;


import com.app.config.filter.JwtTokenValidator;
import com.app.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;


    //HTTPSecurity pasa por todos los filtros
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                //Este desactiva un metodo de proteccion para formularios, en app rest o web no hace falta
                .csrf(csrf -> csrf.disable())
                //Se utiliza cuando te  vas a logear solo con usuario y contraseña
                //Cuando te logeas usando tokens va de una forma diferente
                .httpBasic(Customizer.withDefaults())
                //Aplicaciones web se tiene q trabajar sin estado
                //El tiempo de duracion de logeo va a depender del tiempo de vida del token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {
                    //Si se hace un http request aese endpoint se le permitetodo
                    //Configura los endpoinst publicos primeramente
                    http.requestMatchers(HttpMethod.GET, "/auth/get").permitAll();

                    //solo a ese endpoint va a acceder el que tenga la autorizacion READ LECTURAO
                    //Despues los endpoints privadors
                    http.requestMatchers(HttpMethod.POST, "/auth/post").hasAnyRole("ADMIN", "DEVELOPER");
                    http.requestMatchers(HttpMethod.PATCH, "/auth/patch").hasAnyAuthority("REFACTOR");

                    //Despue se configuran los endpoints NO ESPECIFICADOS
                    //Cualquier otra solicitud que yo no haya especificado arriba, va a hacer lo siguiente (en este caso denegar el acceso)
                    http.anyRequest().denyAll();

                    //Va a acceder cualquier uusario autenticado si tenes credenciales correctas.
                    // http.anyRequest().authenticated();

                })
                //Antes del filtro basico, se va a ejectuar el filtro que hicimos
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //Este provider se conecta a la baase de datos y trae los usuarios
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
/*
Este metodo vos le pones en el parametro la contraseña y cuando ejecutas esa linea
te devuelve la contraseña encriptada

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("1234"));
    }
*/

}

