package gabriel_dev.auth_api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import gabriel_dev.auth_api.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

// a autenticação na aplicação é feita via JWT (JSON Web Token), comum em APIs REST
// deste modo, essa classe será responsável por manipular e controlar esses tokens de autenticação

// @Service indica que essa classe tem a função de encapsular regras e lógica(s) da aplicação
// no caso, a de autenticação e tokens

@Service
public class TokenService {

    @Value("$api.token.secret")
    private String secret;

    public String generateToken(User user) {
        try {
            // HMAC256 é usado para gerar assinaturas de mensagens ou tokens
            // // garantindo integridade e autenticação com base em uma chave secreta compartilhada
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            // cria JWT passando:
            // o nome da aplicação que gerou esse token (Issuer)
            // sujeito que usufruirá deste token (subject)
            // data de expiração
            // assinatura com o algoritmo definido anteriormente
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(this.generateExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error on authentication process");
        }
    }


    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException e) {
            return null; // se der errado, vai retornar nulo
        }
    }

    private Instant generateExpirationDate() {
        // token vai durar 4h
        return LocalDateTime.now().plusHours(4).atZone(ZoneId.of("America/Sao_Paulo")).toInstant();
    }

}
