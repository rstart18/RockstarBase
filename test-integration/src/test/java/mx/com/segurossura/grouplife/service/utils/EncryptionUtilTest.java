package mx.com.segurossura.grouplife.service.utils;

import static org.junit.jupiter.api.Assertions.*;

import mx.com.segurossura.grouplife.infrastructure.exception.customs.EncryptionException;
import mx.com.segurossura.grouplife.infrastructure.utils.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class EncryptionUtilTest {

    @InjectMocks
    private EncryptionUtil encryptionUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Usa ReflectionTestUtils para inyectar el valor de secretKey
        ReflectionTestUtils.setField(encryptionUtil, "secretKey", "1234567890qwerty");
    }

    @Test
    void testDecrypt_success() {
        // Texto encriptado de prueba
        String encryptedText = "Vyx+Z7m2puHW3/tp1nwo7Pmh/+k9C5JWiEc0/JhsWQiJLT5jzOAV4X9hTTZ3yNeHz2X4hNjVI08RGRa3ChwFRdzV7SB81PSms7hdDgenWI40lhnXQ/KnP8WpAr8095tml+MHEbNCWKelyfiflgU+1KRC+s0h7ZqgaGbSUo8UDcKAhJ7+ukM2B4koSN8Q5TQnX9s=";

        // Llama al método decrypt
        Mono<String> decryptedMono = encryptionUtil.decrypt(encryptedText);

        // Verifica el resultado
        StepVerifier.create(decryptedMono)
                .expectNextMatches(decryptedText -> {
                    // Verifica el texto desencriptado esperado
                    assertNotNull(decryptedText);
                    return true; // O realiza una comparación con un valor esperado
                })
                .verifyComplete();
    }

    @Test
    void testDecrypt_failure() {
        // Texto encriptado de prueba incorrecto
        String encryptedText = "invalid-encrypted-text";

        // Llama al método decrypt
        Mono<String> decryptedMono = encryptionUtil.decrypt(encryptedText);

        // Verifica que se produzca un error
        StepVerifier.create(decryptedMono)
                .expectErrorMatches(throwable -> throwable instanceof EncryptionException.RecoverEncryptionException)
                .verify();
    }
}
