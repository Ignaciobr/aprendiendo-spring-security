package com.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

/*
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
                    http.requestMatchers(HttpMethod.GET, "/auth/hello").permitAll();

                    //solo a ese endpoint va a acceder el que tenga la autorizacion READ LECTURAO
                    //Despues los endpoints privadors
                    http.requestMatchers(HttpMethod.GET, "/auth/hello-secured").hasAuthority("CREATE");

                    //Despue se configuran los endpoints NO ESPECIFICADOS
                    //Cualquier otra solicitud que yo no haya especificado arriba, va a hacer lo siguiente (en este caso denegar el acceso)
                    http.anyRequest().denyAll();

                    //Va a acceder cualquier uusario autenticado si tenes credenciales correctas.
                    // http.anyRequest().authenticated();

                })
                .build();
    }
*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity

                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //Este provider se conecta a la baase de datos y trae los usuarios
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    //creo un usuario en vez de traerlo de la base de datos para hacer prueba basica
    @Bean
    public UserDetailsService userDetailsService(){
        List<UserDetails> userDetailslist = new ArrayList<>();

        userDetailslist.add(User.withUsername("Ignacio")
                .password("1234")
                .roles("ADMIN")
                .authorities("READ", "CREATE")
                .build());

        userDetailslist.add(User.withUsername("Daniel")
                .password("1234")
                .roles("USER")
                .authorities("READ")
                .build());

        return new InMemoryUserDetailsManager(userDetailslist);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }



}

