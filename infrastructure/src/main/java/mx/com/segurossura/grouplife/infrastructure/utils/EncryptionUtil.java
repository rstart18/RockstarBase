package mx.com.segurossura.grouplife.infrastructure.utils;

import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.EncryptionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Service
@Slf4j
public class EncryptionUtil {

    @Value("${agentPotal.recover.secretKey}")
    private String secretKey;

    public Mono<String> decrypt(String encryptedText) {

        try {
            log.info("encryptedText {}", encryptedText);

            // Decodificar el texto encriptado de Base64
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);

            // Extraer el IV (nonce) y el texto encriptado
            byte[] iv = Arrays.copyOfRange(encryptedBytes, 0, 12);
            byte[] encryptedData = Arrays.copyOfRange(encryptedBytes, 12, encryptedBytes.length);

            // Convertir la clave secreta a un ArrayBuffer
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // Configurar el GCMParameterSpec
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

            // Desencriptar el texto
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedData);
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

            log.info("decryptedText {}", decryptedText);
            return Mono.just(decryptedText);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return Mono.error(new EncryptionException.RecoverEncryptionException("Error decrypt recover token"));
        }
    }

}
