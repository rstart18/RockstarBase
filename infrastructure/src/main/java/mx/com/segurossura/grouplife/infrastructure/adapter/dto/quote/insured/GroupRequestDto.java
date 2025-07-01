package mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured;

import java.util.List;

public record GroupRequestDto(String number, String name, String ruleType, String occupation, String insuredSum,
                              List<InsuredRequestDto> insureds) {
}
