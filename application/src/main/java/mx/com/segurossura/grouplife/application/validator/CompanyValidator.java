package mx.com.segurossura.grouplife.application.validator;

import mx.com.segurossura.grouplife.domain.model.company.CompanyModality;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageCatalog;
import mx.com.segurossura.grouplife.domain.model.enums.Modality;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public record CompanyValidator() {
    private static final String ERROR_CODE = "VG-VS-003";

    public Mono<Void> canEdit(final FolioRecord folioRecord, final CompanyModality companyModality) {
        if (folioRecord.modality() != null && !folioRecord.modality().equals(companyModality.modality())) {
            return Mono.error(new FolioRecordException.ValidationException(ERROR_CODE, List.of("La modalidad de la compañía existente no puede ser alterada.")));
        }
        return Mono.empty();
    }

    public Mono<ModalityValidation> validate(final CompanyModality companyModality,
                                             final CoverageCatalog coverageCatalog) {

        if (Modality.TRADITIONAL.equals(companyModality.modality())) {
            return this.validateTraditional(companyModality, coverageCatalog);
        } else {
            return this.validateVoluntary(companyModality, coverageCatalog);
        }
    }

    private Mono<ModalityValidation> validateTraditional(final CompanyModality companyModality,
                                                         final CoverageCatalog coverageCatalog) {

        final List<String> errors = new ArrayList<>();

        final int numOpt = companyModality.company().numOperationalInsured() == null ? 0 : companyModality.company().numOperationalInsured();
        final int numAdm = companyModality.company().numAdministrativeInsured() == null ? 0 : companyModality.company().numAdministrativeInsured();
        final int totalInsured = numAdm + numOpt;
        int adminPercentage = 100;
        if (totalInsured > 0) {
            adminPercentage = (int) ((numAdm / (double) totalInsured) * 100);
        }
        final int opPercentage = 100 - adminPercentage;

        if (totalInsured < coverageCatalog.insured().min() || totalInsured > coverageCatalog.insured().max()) {
            errors.add("El total de asegurados debe estar entre " + coverageCatalog.insured().min() + " y " + coverageCatalog.insured().max() + ".");
        }

        if (opPercentage > coverageCatalog.insured().diffAdminOp().percentage()) {
            errors.add("El porcentaje de asegurados operativos no puede exceder el " + coverageCatalog.insured().diffAdminOp().percentage() + "% de los asegurados adminsitrativos.");
        }

        final Integer avgAgeInsured = companyModality.company().averageAgeInsured();
        if (avgAgeInsured < coverageCatalog.age().averageMin() || avgAgeInsured > coverageCatalog.age().averageMax()) {
            errors.add("El promedio de la edad de los asegurados debe estar entre " + coverageCatalog.age().averageMin() + " y " + coverageCatalog.age().averageMax() + ".");
        }

        if (errors.isEmpty()) {
            return Mono.just(new ModalityValidation(coverageCatalog.insured(), coverageCatalog.age(),
                    coverageCatalog.limitBasic(), coverageCatalog.maxGroups(), coverageCatalog.sami(), null, null,
                    coverageCatalog.actuarialAge(), coverageCatalog.standardDeviation(), coverageCatalog.quotient(), coverageCatalog.adjustedAverageAge()
            ));
        } else {
            return Mono.error(new FolioRecordException.ValidationException(ERROR_CODE, errors));
        }
    }

    private Mono<ModalityValidation> validateVoluntary(final CompanyModality companyModality,
                                                       final CoverageCatalog coverageCatalog) {
        final List<String> errors = new ArrayList<>();

        final Company company = companyModality.company();
        if (company.numAdministrativeInsured() != null
                || company.administrativeInsuredSum() != null
                || company.operationalInsuredSum() != null
                || company.numOperationalInsured() != null
                || company.averageAgeInsured() != null) {
            errors.add("Los campos de administrativos y operativos no son obligatorios en modalidad Voluntaria");
        }

        if (errors.isEmpty()) {
            return Mono.just(new ModalityValidation(
                    coverageCatalog.insured(), coverageCatalog.age(), coverageCatalog.limitBasic(), coverageCatalog.maxGroups(),
                    null, coverageCatalog.samiValue(), coverageCatalog.imcValidation(), null, null, null, null
                    ));
        } else {
            return Mono.error(new FolioRecordException.ValidationException(ERROR_CODE, errors));
        }
    }

    public Mono<Company> validateCompanyExistence(final FolioRecord folioRecord) {
        if (folioRecord.company() == null) {
            return Mono.error(new FolioRecordException.CompanyNotFound("VG-VS-008"));
        }
        return Mono.empty();
    }
}
