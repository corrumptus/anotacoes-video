package corrumptus.anotacoes_video.utils.authentication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import corrumptus.anotacoes_video.entity.UserEntity;

@Service
public class UserTokenJWT {
    @Value("${video-anotaocoes.jwt.secret}")
    private String secret;

    private String PROJECT_ISSUER = "video-anotações";

    public String newToken(UserEntity usuario) {
        var algoritmo = Algorithm.HMAC256(secret);

        String token = JWT.create()
            .withIssuer(PROJECT_ISSUER)
            .withSubject(usuario.getLogin())
            .withExpiresAt(expirationDate())
            .sign(algoritmo);

        return token;
    }

    private Instant expirationDate() {
        return LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("-03:00"));
    }

    public String getTokenSubject(String token) {
        Algorithm algoritmo = Algorithm.HMAC256(secret);

        JWTVerifier validator = JWT.require(algoritmo)
            .withIssuer(PROJECT_ISSUER)
            .build();

        return validator.verify(token).getSubject();
    }
}
