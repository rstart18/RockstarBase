package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.QuotationPort;
import mx.com.segurossura.grouplife.domain.model.enums.Modality;
import mx.com.segurossura.grouplife.domain.model.insured.AggregateInsuredGroup;
import mx.com.segurossura.grouplife.domain.model.insured.Insured;
import mx.com.segurossura.grouplife.domain.model.insured.InsuredGroup;
import mx.com.segurossura.grouplife.domain.model.issue.QuoteIssue;
import mx.com.segurossura.grouplife.domain.model.issue.QuoteIssueResponse;
import mx.com.segurossura.grouplife.domain.model.issue.StatusFolio;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.issue.QuoteIssueDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.issue.StatusFolioDataResponse;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.AttributesDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.FolioRecordRequestPolicyDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.PolicyResponse;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.AttributeRequest;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.ClientRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.DtoConstants;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.GroupRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.InsuredGroupRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.InsuredRequestDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.quote.insured.InsuredResponse;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotationGateway implements QuotationPort {
    private static final String COMPANY = "/company";
    private static final String INSUREDS = "/insureds";
    private static final String QUOTE = "/quote";
    private static final String CLIENT = "/client";
    private static final String QUOTE_ISSUE = "/quote-issue";
    private static final String VERIFY_ISSUE = "/status-folio";
    private static final String MONTHS_SALARY = "MESES_SUELDO";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Qualifier("quotationWebClient")
    private final WebClient quotationWebClient;

    @Qualifier("issueWebClient")
    private final WebClient issueWebClient;

    private final QuotationMapper quotationMapper;

    private final InsuredGroupMapper insuredGroupMapper;

    @Qualifier("folioSequenceCircuitBreaker")
    private final CircuitBreaker circuitBreaker;

    @Qualifier("folioSequenceRetry")
    private final Retry retry;

    private static String buildTruncatedFullName(final String firstName, final String middleName, final String lastName,
                                                 final String secondLastName) {
        List<String> nameParts = new ArrayList<>();

        if (firstName != null && !firstName.isBlank()) {
            nameParts.add(firstName);
        }
        if (middleName != null && !middleName.isBlank()) {
            nameParts.add(middleName);
        }
        if (lastName != null && !lastName.isBlank()) {
            nameParts.add(lastName);
        }
        if (secondLastName != null && !secondLastName.isBlank()) {
            nameParts.add(secondLastName);
        }

        String fullName = String.join(" ", nameParts).toUpperCase();
        return fullName.substring(0, Math.min(fullName.length(), 70));
    }

    private static String toUpperCaseOrNull(final String value) {
        return value != null ? value.toUpperCase() : null;
    }

    @Override
    public Mono<String> saveInsureds(final FolioRecord folioRecord,
                                     final AggregateInsuredGroup insureds) {
        return Mono.defer(() -> {
            final InsuredGroupRequestDto request = this.buildInsuredGroupRequestDto(folioRecord, insureds);

            return this.quotationWebClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(INSUREDS)
                            .build())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(InsuredResponse.class)
                    .map(InsuredResponse::getData)
                    .map(InsuredResponse.DataResponse::getMessage)
                    .transformDeferred(RetryOperator.of(this.retry))
                    .transformDeferred(CircuitBreakerOperator.of(this.circuitBreaker))
                    .doOnNext(response -> log.info("Respuesta del servicio de asegurados: {}", response))
                    .onErrorMap(e -> {
                        log.error("Error en el servicio de asegurados para el folio: {}", folioRecord.folio().numberFolio(), e);
                        if (e instanceof final WebClientResponseException we) {
                            return new GatewayException.GatewayClientErrorException(
                                    String.format("Error del cliente: %s, detalles: %s", we.getStatusCode(), we.getResponseBodyAsString())
                            );
                        }
                        return new GatewayException("Error inesperado en el servicio de asegurados.", "VG-GTW-INSUREDS");
                    });
        });
    }

    @Override
    public Mono<Policy> createPolicy(final FolioRecord request, final AggregateInsuredGroup aggregateInsuredGroup) {
        return Mono.defer(() -> {
            final FolioRecordRequestPolicyDto folioRecordRequestPolicyDto = this.buildRequestPolicyDto(request, aggregateInsuredGroup);
            return this.quotationWebClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(COMPANY)
                            .build())
                    .bodyValue(folioRecordRequestPolicyDto)
                    .retrieve()
                    .bodyToMono(PolicyResponse.class)
                    .map(response -> this.quotationMapper.toModel(response.data()))
                    .transformDeferred(RetryOperator.of(this.retry))
                    .transformDeferred(CircuitBreakerOperator.of(this.circuitBreaker))
                    .flatMap(response -> {
                        if (response == null) {
                            log.error("Error del servicio de company: Respuesta nula o vacía");
                            return Mono.error(new GatewayException("Respuesta nula del cliente", "VG-GTW-POLIZY"));
                        }
                        log.info("Respuesta del servicio de la poliza: {}", response);
                        return Mono.just(response);
                    });
        });
    }

    @Override
    public Mono<String> saveClient(final FolioRecord folioRecord, final Policy policy) {
        return Mono.defer(() -> {

            final ClientRequestDto request = this.buildClientRequestDto(folioRecord, policy);

            return this.quotationWebClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(CLIENT)
                            .build())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .transformDeferred(RetryOperator.of(this.retry))
                    .transformDeferred(CircuitBreakerOperator.of(this.circuitBreaker))
                    .flatMap(response -> {
                        if (response == null || response.isEmpty()) {
                            log.error("Error del servicio del cliente: Respuesta nula o vacía");
                            return Mono.error(new GatewayException("Respuesta nula del cliente", "VG-GTW-CLIENT-EMPTY"));
                        }
                        log.info("Respuesta del servicio del cliente: {}", response);
                        return Mono.just(response);
                    });
        });
    }

    @Override
    public Mono<QuoteIssueResponse> issue(final QuoteIssue quoteIssue) {
        return Mono.defer(() -> this.quotationWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(QUOTE_ISSUE)
                        .build())
                .bodyValue(quotationMapper.quoteIssueToRequest(quoteIssue))
                .retrieve()
                .bodyToMono(QuoteIssueDataResponseDto.class)
                .transformDeferred(RetryOperator.of(this.retry))
                .transformDeferred(CircuitBreakerOperator.of(this.circuitBreaker))
                .flatMap(response -> {
                    if (response == null) {
                        log.error("Error del servicio de emision: Respuesta nula o vacía");
                        return Mono.error(new GatewayException("Respuesta nula de la emision", "VG-GTW-ISSUE-EMPTY"));
                    }
                    log.info("Respuesta del servicio de emision: {}", response);
                    return Mono.just(this.quotationMapper.quoteIssueResponseToModel(response.data()));
                }));
    }

    @Override
    public Mono<StatusFolio> verifyIssue(final Long folioNumber) {

        return Mono.defer(() -> this.issueWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(VERIFY_ISSUE + "/" + folioNumber)
                        .build())
                .retrieve()
                .bodyToMono(StatusFolioDataResponse.class)
                .transformDeferred(RetryOperator.of(this.retry))
                .transformDeferred(CircuitBreakerOperator.of(this.circuitBreaker))
                .flatMap(response -> {
                    if (response == null) {
                        log.error("Folio: {}, Error del servicio de emision - Nulo o vacío", folioNumber);
                        return Mono.error(new GatewayException("Respuesta nula de la emision", "VG-GTW-ISSUE-EMPTY"));
                    }
                    StatusFolioDataResponse.StatusFolio respData = response.data();
                    log.info("Folio: {}, Respuesta status emision: {}", folioNumber, respData);

                    return Mono.just(this.quotationMapper.statusFolioResponseToModel(respData));
                }));
    }

    private ClientRequestDto buildClientRequestDto(@NonNull final FolioRecord folioRecord, final Policy policy) {
        final List<AttributeRequest> attributesPersonCte = this.createAttributeRequestPersonCte(folioRecord.client().general());
        final List<AttributeRequest> attributesAddressCte = this.attributesAddressCte(folioRecord.client().address(), folioRecord.client().general().phoneNumber());
        final List<AttributeRequest> attributesContactCte =
                this.createAttributeRequestContactCte(folioRecord.client().general(), "general");
        final List<AttributeRequest> attributesPersonFac =
                this.createAttributeRequestPersonCte(folioRecord.client().invoicing());
        final List<AttributeRequest> attributesAddressFac = this.attributesAddressCte(folioRecord.client().billingAddress(), folioRecord.client().invoicing().phoneNumber());
        final List<AttributeRequest> attributesContactFac =
                this.createAttributeRequestContactCte(folioRecord.client().invoicing(), "invoicing");
        return new ClientRequestDto((int) Math.floor(policy.policyNumber()),
                Integer.parseInt(folioRecord.officeId()), policy.productId(), policy.status(),
                attributesPersonCte, attributesAddressCte, attributesContactCte, attributesPersonFac,
                attributesAddressFac, attributesContactFac);
    }

    private List<AttributeRequest> createAttributeRequestContactCte(final GeneralInfo generalInfo, final String type) {
        final List<AttributeRequest> attributesContactCte = new ArrayList<>();
        final int generalType = generalInfo.typeLegalId();

        attributesContactCte.add(new AttributeRequest(DtoConstants.OTVALOR02, DtoConstants.OTVALOR02_DESC,
                toUpperCaseOrNull(generalInfo.curp())));
        attributesContactCte.add(new AttributeRequest(DtoConstants.OTVALOR14, DtoConstants.OTVALOR14_DESC,
                toUpperCaseOrNull(generalInfo.email())));
        attributesContactCte.add(new AttributeRequest(DtoConstants.OTVALOR19, DtoConstants.OTVALOR19_DESC,
                toUpperCaseOrNull(generalInfo.phoneNumber())));
        attributesContactCte.add(new AttributeRequest(DtoConstants.OTVALOR20, DtoConstants.OTVALOR20_DESC,
                toUpperCaseOrNull(generalInfo.email())));

        // Apoderado Legal
        if (generalType == 2) {
            attributesContactCte.add(new AttributeRequest(DtoConstants.OTVALOR39, DtoConstants.OTVALOR39_DESC,
                    "S"));
            attributesContactCte.add(new AttributeRequest(DtoConstants.OTVALOR64, DtoConstants.OTVALOR64_DESC,
                    toUpperCaseOrNull(generalInfo.legalRepresentativeName())));

            if (generalInfo.birthdate() != null) {
                String formattedDate = generalInfo.birthdate().format(DATE_FORMAT);
                attributesContactCte.add(new AttributeRequest(DtoConstants.OTVALOR89, DtoConstants.OTVALOR89_DESC,
                        formattedDate));
            }
        }

        //Facturacion
        if (type.equals("invoicing")) {
            String taxReformId = generalInfo.taxReform() != null ? generalInfo.taxReform().key() : null;
            attributesContactCte.add(new AttributeRequest(DtoConstants.OTVALOR96, DtoConstants.OTVALOR96_DESC,
                    taxReformId));
            attributesContactCte.add(new AttributeRequest(DtoConstants.OTVALOR97, DtoConstants.OTVALOR97_DESC,
                    generalInfo.receiverCode()));
        }

        return attributesContactCte;
    }

    private List<AttributeRequest> attributesAddressCte(final AddressClient addressClient, final String phoneNumber) {
        final List<AttributeRequest> attributesAddressCte = new ArrayList<>();

        String ordinalDomicile = addressClient.ordinalDomicile() != null ? String.valueOf(addressClient.ordinalDomicile()) : null;

        attributesAddressCte.add(new AttributeRequest(DtoConstants.NMORDDOM, DtoConstants.NMORDDOM,
                ordinalDomicile));
        attributesAddressCte.add(new AttributeRequest(DtoConstants.DSDOMICI, DtoConstants.DSDOMICI,
                toUpperCaseOrNull(addressClient.streetName())));

        attributesAddressCte.add(new AttributeRequest(DtoConstants.NMTELEFO, DtoConstants.NMTELEFO,
                toUpperCaseOrNull(phoneNumber)));

        attributesAddressCte.add(new AttributeRequest(DtoConstants.CDPOSTAL, DtoConstants.CDPOSTAL,
                addressClient.zipCode()));
        attributesAddressCte.add(new AttributeRequest(DtoConstants.OTPOBLAC, DtoConstants.ALCALDIA,
                toUpperCaseOrNull(addressClient.municipality())));
        attributesAddressCte.add(new AttributeRequest(DtoConstants.OTPISO, DtoConstants.NM_NUMBER_RTA,
                toUpperCaseOrNull(addressClient.internalDepartmentNumber())));
        attributesAddressCte.add(new AttributeRequest(DtoConstants.NM_NUMBER, DtoConstants.NUMBER_ABROAD,
                toUpperCaseOrNull(addressClient.streetNumberExt())));
        attributesAddressCte.add(new AttributeRequest(DtoConstants.CDPROVIN, DtoConstants.STATE,
                addressClient.stateId()));
        attributesAddressCte.add(new AttributeRequest(DtoConstants.CDCOLONI, DtoConstants.COLONY,
                addressClient.colonyId()));

        //Defaults
        attributesAddressCte.add(new AttributeRequest(DtoConstants.CDTIPDOM, DtoConstants.CDTIPDOM, "1"));
        attributesAddressCte.add(new AttributeRequest(DtoConstants.CDIDIOMA, DtoConstants.CDIDIOMA, "1"));
        attributesAddressCte.add(new AttributeRequest(DtoConstants.CDPAIS, DtoConstants.CDPAIS, "052"));

        return attributesAddressCte;
    }

    private List<AttributeRequest> createAttributeRequestPersonCte(final GeneralInfo generalInfo) {
        final List<AttributeRequest> attributes = new ArrayList<>();
        final int generalType = generalInfo.typeLegalId();

        attributes.add(new AttributeRequest(DtoConstants.CDIDEPER, DtoConstants.RFC,
                generalInfo.rfc()));
        attributes.add(new AttributeRequest(DtoConstants.OTFISJUR, DtoConstants.TYPE_LEGAL_ID,
                String.valueOf(generalInfo.typeLegalId())));

        if (generalType == 1) {
            attributes.add(new AttributeRequest(DtoConstants.DS_NAME, DtoConstants.NAME,
                    buildTruncatedFullName(generalInfo.name(),
                            generalInfo.secondName(), generalInfo.surname(),
                            generalInfo.secondSurname())));
            attributes.add(new AttributeRequest(DtoConstants.DS_NAME_1, DtoConstants.NAME,
                    toUpperCaseOrNull(generalInfo.name())));
            attributes.add(new AttributeRequest(DtoConstants.DS_NAME_2, DtoConstants.NAME,
                    toUpperCaseOrNull(generalInfo.secondName())));
            attributes.add(new AttributeRequest(DtoConstants.LAST_NAME_1, DtoConstants.NAME,
                    toUpperCaseOrNull(generalInfo.surname())));
            attributes.add(new AttributeRequest(DtoConstants.LAST_NAME_2, DtoConstants.NAME,
                    toUpperCaseOrNull(generalInfo.secondSurname())));

            String formattedDate = generalInfo.birthdate().format(DATE_FORMAT);
            attributes.add(new AttributeRequest(DtoConstants.BIRTH_DATE, DtoConstants.NAME, formattedDate));
        } else if (generalType == 2) {
            attributes.add(new AttributeRequest(DtoConstants.DS_NAME, DtoConstants.NAME,
                    toUpperCaseOrNull(generalInfo.businessName())));
            attributes.add(new AttributeRequest(DtoConstants.DS_NAME_1, DtoConstants.NAME, ""));
            attributes.add(new AttributeRequest(DtoConstants.DS_NAME_2, DtoConstants.NAME, ""));
            attributes.add(new AttributeRequest(DtoConstants.LAST_NAME_1, DtoConstants.NAME, ""));
            attributes.add(new AttributeRequest(DtoConstants.LAST_NAME_2, DtoConstants.NAME, ""));

            String formattedDate = generalInfo.constitutionDate().format(DATE_FORMAT);
            attributes.add(new AttributeRequest(DtoConstants.BIRTH_DATE, DtoConstants.NAME, formattedDate));
        }
        attributes.add(new AttributeRequest(DtoConstants.OT_GENDER, DtoConstants.OT_GENDER,
                toUpperCaseOrNull(generalInfo.gender())));
        return attributes;
    }

    private InsuredGroupRequestDto buildInsuredGroupRequestDto(
            final FolioRecord folioRecord,
            final AggregateInsuredGroup aggregateInsuredGroup) {

        final Map<String, GroupRequestDto> groupRequestMap = this.groupInsureds(folioRecord,
                aggregateInsuredGroup.groups());
        final List<GroupRequestDto> groupRequests = new ArrayList<>(groupRequestMap.values());
        return this.createInsuredGroupRequestDto(folioRecord, groupRequests);
    }

    private Map<String, GroupRequestDto> groupInsureds(final FolioRecord folioRecord, final List<InsuredGroup> groups) {
        final Map<String, GroupRequestDto> groupRequestMap = new HashMap<>();

        AtomicInteger situationNumberCounter = new AtomicInteger(1);
        groups.forEach(group -> group.insureds().forEach(insured -> {
            final String key = this.createGroupKey(group, insured);

            groupRequestMap.computeIfAbsent(key, k -> this.createGroupRequestDto(folioRecord, group, insured))
                    .insureds().add(this.mapToInsuredRequest(folioRecord, insured, group, situationNumberCounter.getAndIncrement()));
        }));

        return groupRequestMap;
    }

    private String createGroupKey(final InsuredGroup group, final Insured insured) {
        return group.groupNumber() + "-" + insured.occupation();
    }

    private GroupRequestDto createGroupRequestDto(final FolioRecord folioRecord,
                                                  final InsuredGroup group,
                                                  final Insured insured) {
        final GroupVg folioGroup = folioRecord.groups().stream()
                .filter(fg -> fg.groupNumber().equals(group.groupNumber()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró un grupo en FolioRecord con número: " + group.groupNumber()));
        final String insuredSum = this.getInsuredSumByOccupation(folioGroup, insured.occupation());
        return new GroupRequestDto(
                String.valueOf(group.groupNumber()),
                group.name(),
                group.insuredSumRule(),
                insured.occupation(),
                insuredSum,
                new ArrayList<>()
        );
    }

    private String getInsuredSumByOccupation(final GroupVg folioGroup, final String occupation) {
        if ("ADMINISTRATIVOS".equalsIgnoreCase(occupation)) {
            return String.valueOf(folioGroup.administrativeInsuredSum());
        } else if ("OPERATIVOS".equalsIgnoreCase(occupation)) {
            return String.valueOf(folioGroup.operationalInsuredSum());
        } else {
            throw new IllegalArgumentException("Ocupación desconocida: " + occupation);
        }
    }

    private InsuredGroupRequestDto createInsuredGroupRequestDto(
            final FolioRecord folioRecord,
            final List<GroupRequestDto> groupRequests) {
        return new InsuredGroupRequestDto(
                folioRecord.folio().numberFolio().intValue(),
                folioRecord.modality().getMessageFormat(),
                folioRecord.company().businessActivity().key(),
                groupRequests
        );
    }

    private InsuredRequestDto mapToInsuredRequest(final FolioRecord folioRecord, final Insured insured,
                                                  final InsuredGroup group, final int situationNumber) {
        final GroupVg folioGroup = folioRecord.groups().stream()
                .filter(fg -> fg.groupNumber().equals(group.groupNumber()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró un grupo en FolioRecord con número: " + group.groupNumber()));
        Integer salaryMonthValue = null;
        if (MONTHS_SALARY.equalsIgnoreCase(folioGroup.groupType())) {
            salaryMonthValue = folioGroup.salaryMonth();
        }

        InsuredRequestDto insuredRequestDto = this.insuredGroupMapper.toModel(insured);
        final Integer monthlySalary = insured.monthlySalary() != null ? insured.monthlySalary().intValue() : null;
        insuredRequestDto = insuredRequestDto.toBuilder()
                .kindshipId("T")
                .insuredSumSalary(String.valueOf(monthlySalary))
                .salaryMonth(String.valueOf(salaryMonthValue))
                .situationNumber(situationNumber)
                .build();

        return insuredRequestDto;
    }

    private FolioRecordRequestPolicyDto buildRequestPolicyDto(final FolioRecord request, final AggregateInsuredGroup aggregateInsuredGroup) {
        return this.quotationMapper.domainToRequestDto(request)
                .toBuilder()
                .attributes(this.createAttributesFromCoverage(request, aggregateInsuredGroup))
                .build();
    }

    private List<AttributesDto> createAttributesFromCoverage(final FolioRecord folioRecord,
                                                             @NonNull final AggregateInsuredGroup aggregateInsuredGroup) {
        final List<AttributesDto> attributes = new ArrayList<>();
        attributes.add(new AttributesDto("OTVALOR05", folioRecord.groupId()));
        attributes.add(new AttributesDto("OTVALOR06", folioRecord.subgroupId()));
        attributes.add(new AttributesDto("OTVALOR67", folioRecord.company().businessActivity().key()));
        attributes.add(new AttributesDto("OTVALOR17",
                this.mapAdministrationType(folioRecord.quotationDetails().administrationType().name())));
        attributes.add(new AttributesDto("OTVALOR68", this.mapModalityToCode(folioRecord.modality())));
        attributes.add(new AttributesDto("OTVALOR32", String.valueOf(aggregateInsuredGroup.standardDeviation())));
        attributes.add(new AttributesDto("OTVALOR54", String.valueOf(aggregateInsuredGroup.sami())));
        attributes.add(new AttributesDto("OTVALOR71", String.valueOf(aggregateInsuredGroup.actuarialAge())));
        attributes.add(new AttributesDto("OTVALOR73", String.valueOf(aggregateInsuredGroup.quotient())));
        attributes.add(new AttributesDto("OTVALOR22", String.valueOf(aggregateInsuredGroup.averageAge())));
        attributes.add(new AttributesDto("OTVALOR31",
                this.mapPromoterCommissionPercentageToString(folioRecord.quotationDetails().agentCommissionPercentage())));
        attributes.add(new AttributesDto("OTVALOR43",
                this.mapPromoterCommissionPercentageToString(folioRecord.quotationDetails().promoterCommissionPercentage())));
        attributes.add(new AttributesDto("OTVALOR56",
                this.mapPromoterCommissionPercentageToString(folioRecord.quotationDetails().agentCommissionPercentage())));
        attributes.add(new AttributesDto("OTVALOR57",
                this.mapPromoterCommissionPercentageToString(folioRecord.quotationDetails().promoterCommissionPercentage())));
        attributes.add(new AttributesDto("OTVALOR70", String.valueOf(aggregateInsuredGroup.adjustedAverageAge())));
        attributes.add(new AttributesDto("OTVALOR72", String.valueOf(aggregateInsuredGroup.diffActuarialAverageAge())));
        return attributes;
    }

    private String mapPromoterCommissionPercentageToString(final Double percentage) {
        if (percentage == null) {
            return "";
        }
        return String.valueOf((int) Math.round(percentage * 100));
    }

    private String mapModalityToCode(final Modality modality) {
        if (modality == null) {
            throw new IllegalArgumentException("La modalidad no puede ser nula");
        }
        return switch (modality.getMessageFormat()) {
            case "TRADICIONAL" -> "001";
            case "VOLUNTARIA" -> "002";
            default -> throw new IllegalArgumentException("Modalidad desconocida: " + modality.getMessageFormat());
        };
    }

    private String mapAdministrationType(final String administrationTypeName) {
        if (administrationTypeName == null || administrationTypeName.isBlank()) {
            throw new IllegalArgumentException("El tipo de administración no puede ser nulo o vacío");
        }

        return switch (administrationTypeName.toUpperCase()) {
            case "DETALLADA" -> "D";
            case "AUTOADMINISTRADA" -> "A";
            default ->
                    throw new IllegalArgumentException("Tipo de administración desconocido: " + administrationTypeName);
        };
    }

}
