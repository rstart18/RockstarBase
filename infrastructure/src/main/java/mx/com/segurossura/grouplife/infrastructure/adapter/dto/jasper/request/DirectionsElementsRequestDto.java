package mx.com.segurossura.grouplife.infrastructure.adapter.dto.jasper.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record DirectionsElementsRequestDto(
        String nomEmp,
        String dirEmp1,
        String dirEmp2,
        String dirEmp3,
        String dirEmp4,
        String telEmp,
        String acrEmp,
        String rfcEmp,
        String pagWeb,
        String telEmp1,
        String telEmp2,
        String corrEmp,
        String pagweb1,
        @JsonProperty("CDMX") String cdmx,
        String telUnat,
        String telUnatP,
        String correoUnat
) {
}
