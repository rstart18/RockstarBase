package mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured;

import lombok.Builder;

import java.util.List;
@Builder(toBuilder = true)
public record InsuredGroupRequestDto(Integer folio, String modality, String businessActivity,
                                     List<GroupRequestDto> groupInsureds) {
}
