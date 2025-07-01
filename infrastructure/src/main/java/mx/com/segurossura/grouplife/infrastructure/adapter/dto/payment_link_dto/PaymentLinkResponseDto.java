package mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto;

import java.time.LocalDate;

public record PaymentLinkResponseDto(String urlRedirect, LocalDate validity, Boolean success) {
}
