package mx.com.segurossura.grouplife.domain.model.validation;

import java.time.LocalDate;

public record ValidateRFCRequest (String typeLegal, String rfc, String name, String surname, String secondSurname, LocalDate birthdate) {
}
