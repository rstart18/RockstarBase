package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

public record AgesDto(int minDays, int maxDays, int minYears, int maxYears, Integer percentage, String action) {
}
