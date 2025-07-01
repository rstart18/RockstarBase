package mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsuredResponse {
    private DataResponse data;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class DataResponse {
        private String message;

    }
}
