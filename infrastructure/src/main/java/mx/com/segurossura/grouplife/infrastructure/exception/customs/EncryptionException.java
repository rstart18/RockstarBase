package mx.com.segurossura.grouplife.infrastructure.exception.customs;

import lombok.Getter;

@Getter
public class EncryptionException extends RuntimeException {

    private final String code;

    public EncryptionException(final String message, final String code) {
        super(message);
        this.code = code;
    }

    public static class RecoverEncryptionException extends EncryptionException {
        public RecoverEncryptionException(final String message) {
            super(message, "VG-ENCRYPTION-RECOVER");
        }
    }

    public static class JwtException extends EncryptionException {
        public JwtException(final String message) {
            super(message, "VG-ENCRYPTION-JWT");
        }
    }
}
