package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import java.util.List;

public record GroupedDto(String title, String text, List<String> siblingCoverages) {
}
