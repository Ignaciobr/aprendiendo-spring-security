package com.app.config.filter;


import com.app.util.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

//La clase que va a verificar que el token sea valido
// Ejecuta este filtro por cada peticion quew se haga, se garantiza de que siempre se mande un token
public class JwtTokenValidator extends OncePerRequestFilter {


    private JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {


        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(jwtToken != null ){
            //Con esto extramos el string sin el Bearer  que son 6 lettras mas el espacio = 7
            jwtToken = jwtToken.substring(7);

            //Lo validamos
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

            //Obtengo el usuario
            String username = jwtUtils.extractUsername(decodedJWT);
            // obtengo los permisos que tiene el usuario
            String stringAuthorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

            //tomar los permisos separados por coma y los convierte en una listqa de permisos
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);

            SecurityContext context = SecurityContextHolder.getContext();
            //
            Authentication authentication = new UsernamePasswordAuthenticationToken(username ,null, authorities);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            //Le dimos autorizacion al usuario

        }
        //Si el filtro no pasa por el if,m sigue con el otro filtro
        filterChain.doFilter(request, response);


    }
}
