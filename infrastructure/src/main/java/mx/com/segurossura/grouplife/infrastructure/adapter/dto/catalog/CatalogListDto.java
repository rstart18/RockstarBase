package mx.com.segurossura.grouplife.infrastructure.adapter.dto.catalog;

import mx.com.segurossura.grouplife.openapi.model.CatalogItemDto;

import java.util.List;

public record CatalogListDto(List<CatalogItemDto> data) {
}

