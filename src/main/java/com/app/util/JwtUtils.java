package com.app.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
//Aca va el json web token
public class JwtUtils {

    //Se necesita un usuario generador del token y una clave privada

    //en google "sha generator key"
    //https://tools.keycdn.com/sha256-online-generator

    @Value("${security.jwt.key.private}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    public String createToken(Authentication authentication) {

        //Se va a usar la clave privada encriptada
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        //Se va a autenticar el usuario

        String username = authentication.getPrincipal().toString();
        //Va a obtener todas las autorizaciones y las convierte a un string
        // y con el collector joinin toma cada una de los permisos y los separa por coma ","
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String jtwToken = JWT.create()
                //Aca va ael usuario que genera el token
                .withIssuer(this.userGenerator)
                //a quien se le va a generar, en este caso el username
                .withSubject(username)
                //Los pérmisos que se les vaa dar al usuario
                .withClaim("authorities", authorities)
                //la fecha. el momento en donde se genera el token
                .withIssuedAt(new Date())
                //El momento en el que expira el token en milisegundos
                .withExpiresAt(new Date(System.currentTimeMillis() + 1800000))
                //aSIGNAMOS UN  ID random A nuestro token
                .withJWTId(UUID.randomUUID().toString())
                //a partir de que momento el token es valido
                .withNotBefore(new Date(System.currentTimeMillis()))
                //Le ponemos la firma
                .sign(algorithm);


        return jtwToken;
    }

//Devuelve el JWT  decodificado
    public DecodedJWT validateToken(String token) {
        try {
            //Para decodificar se necesita el algoritmo con el cual se codifico y su clave privada
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            //El metodo verifica el token, si es invalido manda la excepcion pero si sale bien, devuelve el jwt decodidifcad
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;

        }catch (JWTVerificationException exception){
            //Si la verificacion manda el mensaje es porque el token es invalido
            throw new JWTVerificationException("Token invalid, not Authorized");
        }
    }


    //Devuelve el usuario del token
    public String extractUsername(DecodedJWT decodedJWT) {
        //Dwevuelve el username que se le asigno el token
        return decodedJWT.getSubject().toString();
    }



    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {

        return decodedJWT.getClaim(claimName);
    }

    /**
    * Devuelve todos los claims contenidos en el token JWT.
    *
    * Un claim es un dato almacenado dentro del token,
    * como el usuario, roles, permisos, fecha de emisión
    * o fecha de expiración.
    *
     * {
     *   "sub": "ignacio",
     *   "authorities": ["READ", "WRITE"],
     *   "role": "ADMIN"
     *
     * getSpecificClaim(jwt, "role")
     *
     * devuelve "ADMIN"
    **/

    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }

}


