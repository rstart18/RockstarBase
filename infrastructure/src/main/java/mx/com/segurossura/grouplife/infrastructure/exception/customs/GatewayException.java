package mx.com.segurossura.grouplife.infrastructure.exception.customs;

import lombok.Getter;

@Getter
public class GatewayException extends RuntimeException {

    private final String code;

    public GatewayException(final String message, final String code) {
        super(message);
        this.code = code;
    }

    public static class GatewayUnauthorizedException extends GatewayException {
        public GatewayUnauthorizedException(final String message) {
            super(message, "VG-GTW-UNAUTHORIZED");
        }
    }

    public static class GatewayClientErrorException extends GatewayException {
        public GatewayClientErrorException(final String message) {
            super(message, "VG-GTW-CLIENT-ERROR");
        }
    }

    public static class GatewayServerErrorException extends GatewayException {
        public GatewayServerErrorException(final String message) {
            super(message, "VG-GTW-SERVER-ERROR");
        }
    }
}
