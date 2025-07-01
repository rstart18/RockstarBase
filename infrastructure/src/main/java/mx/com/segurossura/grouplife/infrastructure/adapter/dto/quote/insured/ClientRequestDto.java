package mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured;

import java.util.List;

public record ClientRequestDto(Integer policyNumber, Integer officeId, Integer productId, String status,
                               List<AttributeRequest> attributesPersonCte,
                               List<AttributeRequest> attributesAddressCte,
                               List<AttributeRequest> attributesContactCte, List<AttributeRequest> attributesPersonFac,
                               List<AttributeRequest> attributesAddressFac, List<AttributeRequest> attributesContactFac) {
}
