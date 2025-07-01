package mx.com.segurossura.grouplife.infrastructure.adapter.dto.folio;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FolioResponseDto {
    private FolioData data;

    @Setter
    @Getter
    public static class FolioData {
        private Long numberFolio;
        private String partnerId;
        private String product;

    }
}

