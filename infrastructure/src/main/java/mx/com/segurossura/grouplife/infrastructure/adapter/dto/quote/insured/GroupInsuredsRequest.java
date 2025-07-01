package mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured;

import lombok.Builder;

import java.util.List;
@Builder(toBuilder = true)
public record GroupInsuredsRequest(List<GroupRequestDto> groupInsureds) {
}
