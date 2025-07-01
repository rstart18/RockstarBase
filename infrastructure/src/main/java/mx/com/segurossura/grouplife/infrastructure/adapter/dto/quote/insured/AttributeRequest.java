package mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AttributeRequest {
        private String code;
        private String description;
        private String value;
}
