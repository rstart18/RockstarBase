package mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ReportQuotationRequestDto(Reportes reporte, DirectionsElementsRequestDto directionsElements) {
    public record Reportes(ReportQuotationDataRequestDto data, Parametros parametros, String plantilla) {
    }

    public record Parametros(@JsonProperty("LOGO") String logo, @JsonProperty("SUBREPORT_DIR") String subreportDir) {
    }
}