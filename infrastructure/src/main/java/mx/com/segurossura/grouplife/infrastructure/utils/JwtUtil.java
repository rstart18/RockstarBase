package mx.com.segurossura.grouplife.infrastructure.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.component.JwtUtilDto;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.EncryptionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class JwtUtil {

    @Value("${agentPotal.jwt.secretKey}")
    private String jwtSecretKey;

    public Mono<JwtUtilDto> validateJwt(String token) {

        try {
            log.info("JWT to verify {}", token);

            Algorithm algorithm = Algorithm.HMAC256(jwtSecretKey);
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .build()
                    .verify(token);

            log.info("Jwt Token {}", decodedJWT.getClaims());

            final Claim valueClaim = decodedJWT.getClaim("value");
            final Claim originClaim = decodedJWT.getClaim("origen");

            log.info("Jwt Token valueClaim " + valueClaim);
            log.info("Jwt Token originClaim " + originClaim);

            return Mono.just(JwtUtilDto.builder()
                    .value(valueClaim.asString())
                    .origen(originClaim.asString()).build()
            );

        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return Mono.error(new EncryptionException.JwtException("Error valid jwt token"));
        }
    }

}
