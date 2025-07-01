package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import java.util.List;

public record CatalogDataDto(String product, String description, List<CoverageCatalogDto> coverages) {
}
