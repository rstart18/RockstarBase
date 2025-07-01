package mx.com.segurossura.grouplife.service.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.component.JwtUtilDto;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.EncryptionException;
import mx.com.segurossura.grouplife.infrastructure.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    private final String secretKey = "testSecretKey";
    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        // Establecemos el valor de jwtSecretKey usando ReflectionTestUtils
        ReflectionTestUtils.setField(jwtUtil, "jwtSecretKey", secretKey);
    }

    @Test
    public void validateJwt_whenTokenIsValid_shouldReturnClaimValue() {

        final String token = this.generateToken(secretKey);
        final JwtUtilDto expectedValue = JwtUtilDto.builder()
                .value("expectedValue")
                .origen("externo").build();

        // Usamos el JWT.require para validar el token
        Mono<JwtUtilDto> result = jwtUtil.validateJwt(token);

        // Verificamos el resultado utilizando StepVerifier
        StepVerifier.create(result)
                .expectNext(expectedValue)
                .verifyComplete();
    }

    @Test
    public void validateJwt_whenTokenIsInvalid_shouldReturnError() {

        Mono<JwtUtilDto> result = jwtUtil.validateJwt("invalidToken");

        StepVerifier.create(result)
                .expectError(EncryptionException.JwtException.class)
                .verify();
    }

    private String generateToken(String secretKey) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create().withClaim("value", "expectedValue")
                .withClaim("origen", "externo")
                .withIssuedAt(new Date()).sign(algorithm);
    }
}
