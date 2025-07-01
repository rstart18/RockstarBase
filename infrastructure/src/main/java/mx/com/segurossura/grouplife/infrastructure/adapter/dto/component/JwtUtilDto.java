package mx.com.segurossura.grouplife.infrastructure.adapter.dto.component;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class JwtUtilDto {
    private String value;
    private String origen;
}
