package mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured;

import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record InsuredRequestDto(String name, String secondName, String surname, String secondSurname, LocalDate birthDate,
                                Integer age, String gender, String questionnaire, String kindshipId, String rfc,
                                String email, String phoneNumber, Integer situationNumber, String salaryMonth,
                                String insuredSumSalary) {
}
