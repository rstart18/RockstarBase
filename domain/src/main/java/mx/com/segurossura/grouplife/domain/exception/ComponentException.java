package mx.com.segurossura.grouplife.domain.exception;

import lombok.Getter;

import java.io.Serial;

public class ComponentException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -256664424537915563L;

    ComponentException(final String message) {
        super(message);
    }

    @Getter
    public static class FolioBadStatus extends ComponentException {
        @Serial
        private static final long serialVersionUID = 9176662754714534366L;
        private final String code;

        public FolioBadStatus(final String code, final String message) {
            super(message);
            this.code = code;
        }
    }
}
