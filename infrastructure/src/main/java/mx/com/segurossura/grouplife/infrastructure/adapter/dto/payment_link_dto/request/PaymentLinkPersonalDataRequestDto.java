package mx.com.segurossura.grouplife.infrastructure.adapter.dto.payment_link_dto.request;

import java.time.LocalDate;

public record PaymentLinkPersonalDataRequestDto(String typeLegalId, String rfc, String businessName, String name,
                                                String secondName, String surname, String secondSurname,
                                                LocalDate birthdate, String gender, String email, String phoneNumber) {
}
