package mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote;

import java.util.List;

public record GroupsRequestDto(String number, String name, String ruleType, int salaryMonth, int averageAgeInsured
        , int numAdministrativeInsured, int numOperationalInsured, List<CoveragesDto> coverages) {
}
