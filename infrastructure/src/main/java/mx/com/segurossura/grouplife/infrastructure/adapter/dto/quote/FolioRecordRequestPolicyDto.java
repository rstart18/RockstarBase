package mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
@Builder(toBuilder = true)
public record FolioRecordRequestPolicyDto(int officeId, int productId, String folio, String companyName,
                                          String modality, String agentCode,
                                          LocalDate effectiveDate, List<GroupsRequestDto> groups,
                                          List<AttributesDto> attributes) {
}
