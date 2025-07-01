package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.controller.testdata.TestFixturesClient;
import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.AgeLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.Insured;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSumLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredValidation;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.domain.model.coverage.YearLimit;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.RecordFolio;
import mx.com.segurossura.grouplife.openapi.model.AddressDto;
import mx.com.segurossura.grouplife.openapi.model.ClientRequestDto;
import mx.com.segurossura.grouplife.openapi.model.CreateClientResponseDto;
import mx.com.segurossura.grouplife.openapi.model.GeneralInfoDto;
import mx.com.segurossura.grouplife.openapi.model.GeneralInfoTaxReformDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorDto;
import mx.com.segurossura.grouplife.openapi.model.StandardErrorResponseDto;
import mx.com.segurossura.grouplife.openapi.model.StandardResponseMessageDto;
import mx.com.segurossura.grouplife.openapi.model.TypeLegalIdDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateClientIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/folio/{numberFolio}/client";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";

    private static Stream<Arguments> createClientWithDataDynamicInvalidBusinessRulesType1() {
        return Stream.of(
                Arguments.of("Request with invalid rfc for person type 1 return ClientDataException", "PEGJ851106LN3", "Garcia",
                        "Carlos", "1985-08-15", "PGJH010101HNGRLN04", "EMP241106MG4", "company", "1985-08-15", "juan " +
                                "Company", "El nombre no puede ser nulo"),
                Arguments.of("Request without secondSurname returns ClientDataException", "PEGJ851106LN3", "Garcia",
                        "Carlos", "1985-08-15", "PGJH010101HNGRLN04", "EMP241106MG4", "company", "1985-08-15", "juan " +
                                "Company", "El nombre no puede ser nulo"),
                Arguments.of("Request with invalid rfc for person type 2 return ClientDataException", "PEGJ851106LN3",
                        "Garcia", "Carlos", "1985-08-15", "PGJH010101HNGRLN04", "EMP241106MG4", "company", "1985-08-15",
                        "juan Company", "El nombre no puede ser nulo"),
                Arguments.of("Request without businessName in person type 2 returns ClientDataException", "PEGJ851106LN3", "Garcia",
                        "Carlos", "1985-08-15", "PGJH010101HNGRLN04", "EMP241106MG4", null, "1985-08-15",
                        "juanCompany", "El nombre no puede ser nulo")
        );
    }

    private static Stream<Arguments> createClientWithDataDynamicInvalidDtoInGeneralInFormat() {
        final String errorName = "camilatetetetghnjrqgpf";
        final String description = "debe tener entre 0 y 20 caracteres.";

        return Stream.of(
                Arguments.of("Request with bad name in format returns code VG-INPUT-002", "PEGJ851106LN3",
                        "alex@", "juan.perez@email.com", "5551234567", "general.name",
                        formatErrorMessage("Nombre"),
                        "surname", "camila", "empresa 24"),

                Arguments.of("Request with bad name with more than 20 characters returns code VG-INPUT-002", "PEGJ851106LN3",
                        errorName, "juan.perez@email.com", "5551234567", "general.name",
                        description, "surname", "camila", "empresa 24"),

                Arguments.of("Request with bad surname in format returns code VG-INPUT-002", "PEGJ851106LN3",
                        "alex", "juan.perez@email.com", "5551234567", "general.surname",
                        formatErrorMessage("Primer apellido"),
                        "suraname@", "camila", "empresa 24"),

                Arguments.of("Request with surname with more than 20 characters returns code VG-INPUT-002",
                        "PEGJ851106LN3", "alex", "juan.perez@email.com", "5551234567", "general.surname",
                        description, errorName, "camila", "empresa 24"),

                Arguments.of("Request with bad secondSurname in format returns code VG-INPUT-002", "PEGJ851106LN3",
                        "alex", "juan.perez@email.com", "5551234567", "general.secondSurname",
                        formatErrorMessage("Segundo apellido"), // Inserta dinámicamente
                        "suraname", "camila@", "empresa 24"),

                Arguments.of("Request secondSurname with more than 20 characters returns code VG-INPUT-002",
                        "PEGJ851106LN3", "alex", "juan.perez@email.com", "5551234567", "general.secondSurname",
                        description, "suraname", errorName, "empresa 24")
        );
    }

    private static String formatErrorMessage(String fieldName) {
        return fieldName + " no cumple con el formato requerido.";
    }

    private static Stream<Arguments> createClientWithDataDynamicInvalidDtoInGeneral() {
        final String errorNotNull = "no debe ser nulo.";
        return Stream.of(
                Arguments.of("Request without rfc returns code VG-INPUT-002", TypeLegalIdDto.NUMBER_1, null, "juan" +
                                ".perez@email.com",
                        "5551234567", "general.rfc", errorNotNull),
                Arguments.of("Request without email returns code VG-INPUT-002", TypeLegalIdDto.NUMBER_1, "PEGJ851106LN3", null,
                        "5551234567", "general.email", errorNotNull),
                Arguments.of("Request without phoneNumber returns code VG-INPUT-002", TypeLegalIdDto.NUMBER_1, "PEGJ851106LN3", "juan.perez@email.com",
                        null, "general.phoneNumber", errorNotNull)

        );
    }

    private static Stream<Arguments> createClientWithDataDynamicInvalidDtoInAddress() {
        final String errorNotNull = "no debe ser nulo.";
        return Stream.of(
                Arguments.of("Request without streetName in the address returns VG-INPUT-002", null, "123", "12345", "Jalisco",
                        "Guadalajara", "Downtown", "address.streetName", errorNotNull),
                Arguments.of("Request without streetNumberExt in the address returns VG-INPUT-002", "Calle", null, "12345", "Jalisco",
                        "Guadalajara", "Downtown", "address.streetNumberExt", errorNotNull),
                Arguments.of("Request without zipCode in the address returns VG-INPUT-002", "Calle", "123", null, "Jalisco",
                        "Guadalajara", "Downtown", "address.zipCode", errorNotNull),
                Arguments.of("Request without stateId in the address returns VG-INPUT-002", "Calle", "123", "12345", null,
                        "Guadalajara", "Downtown", "address.stateId", errorNotNull),
                Arguments.of("Request without municipality in the address returns VG-INPUT-002", "Calle", "123", "12345", "Jalisco",
                        null, "Downtown", "address.municipality", errorNotNull),
                Arguments.of("Request without colonyId in the address returns VG-INPUT-002", "Calle", "123", "12345", "Jalisco",
                        "Guadalajara", null, "address.colonyId", errorNotNull)
        );
    }

    private static Stream<Arguments> createClientWithDataDynamicInvalidBusinessRulesType2() {
        return Stream.of(
                Arguments.of("creando cliente moral sin businessName retorna exception", null, "2024-11-06",
                        "carlos", "2024-11-06" , "El nombre de la empresa no puede ser nulo"),
                Arguments.of("creando cliente moral sin legalRepresentativeName retorna exception", "empresa",  "2024-11-06",
                        null, "2024-11-06" , "El nombre del representante legal no puede ser nulo"),
                Arguments.of("creando cliente moral sin constitutionDate retorna exception", "empresa",  null,
                        "carlos", "2024-11-06" , "La fecha de constitución no puede ser nula"),
                Arguments.of("creando cliente moral sin birthdate retorna exception", "empresa",  "2024-11-06",
                        "carlos", null, "La fecha de nacimiento no puede ser nula")
        );
    }

    private static Stream<Arguments> createClientWithDataDynamicInvalidInGeneralInfoTypeOne() {
        return Stream.of(
                Arguments.of("creando cliente fisico sin secondSurname retorna exception", null, "alex",
                        "PGJH010101HNGRLN04", GeneralInfoDto.GenderEnum.M, "andres", "2024-11-06", "El segundo apellido no puede ser nulo"),
                Arguments.of("creando cliente sin surname retorna exception", "alez", null,
                        "PGJH010101HNGRLN04", GeneralInfoDto.GenderEnum.M, "andres", "2024-11-06", "El apellido no puede ser nulo"),
                Arguments.of("creando cliente sin curp retorna exception", "alez", "andres",
                        null, GeneralInfoDto.GenderEnum.M, "andres", "2024-11-06", "Curp no puede ser nulo"),
                Arguments.of("creando cliente sin Gender retorna exception", "alez", "andres",
                        "PGJH010101HNGRLN04", null , "andres", "2024-11-06", "El género no puede ser nulo"),
                Arguments.of("creando cliente sin name retorna exception", "alez", "andres",
                        "PGJH010101HNGRLN04", GeneralInfoDto.GenderEnum.M, null, "2024-11-06", "El nombre no puede ser nulo")
        );
    }

    private static Stream<Arguments> createClientWithDataDynamic() {
        final GeneralInfoDto generalInfo = TestFixturesClient.createTypePerson1(TypeLegalIdDto.NUMBER_1,
                "PEGJ851106LN3", "1985-08-15", "juan.perez@email.com", "5551234567", "Perez", "Garcia",
                "Carlos", "carlos", "PEGJ851106HASRRN08", GeneralInfoDto.GenderEnum.M);
        final GeneralInfoDto invoicing = TestFixturesClient.createInvoicingType2(TypeLegalIdDto.NUMBER_2, "EMP241106MG4",
                "empresa 23", "1985-08-15", "Juan", "1985-08-15", "juan.perez@email.com", "5551234567",
                new GeneralInfoTaxReformDto("key", "value"), "12345", GeneralInfoDto.GenderEnum.E);
        final AddressDto addressDto = TestFixturesClient.createAddressDto("Main St", "123", "B", "12345", "Jalisco",
                "Guadalajara", "Downtown");
        final AddressDto addressOptional = TestFixturesClient.createAddressOptional("Main St", "123", "12345",
                "Jalisco", "Guadalajara", "Downtown");
        final AddressDto billingAddress = TestFixturesClient.createAddressDto("Main St", "123", "B", "12345", "Jalisco",
                "Guadalajara", "Downtown");
        final GeneralInfoDto generalInfoOptional = TestFixturesClient.createTypePerson1WithoutDataOptional(TypeLegalIdDto.NUMBER_1,
                "PEGJ851106LN3", "1985-08-15", "juan.perez@email.com", "5551234567", "Perez", "Garcia",
                "Carlos", "PEGJ851106HASRRN08", GeneralInfoDto.GenderEnum.M);
        return Stream.of(
                Arguments.of("The customer with TypePerson1 in general and TypePerson2 in invoicing that has complete data must return Created", generalInfo, addressDto,
                        invoicing, billingAddress),
                Arguments.of("Client with TypePerson1 without optional fields and TypePerson2 should return Created",
                        generalInfoOptional, addressDto, invoicing, billingAddress),
                Arguments.of("Client with complete data and optional address fields should return Created",
                        generalInfo, addressOptional, invoicing, billingAddress)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createClientWithDataDynamic")
    void test_createClient_withRequestValid_shouldReturnCreateClientResponseDtoAnsStatusCreated(final String testName,
                                                                                               final GeneralInfoDto general,
                                                                                               final AddressDto address,
                                                                                               final GeneralInfoDto invoicing,
                                                                                               final AddressDto billingAddress) {
        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA",
                true, null,
                List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null,null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("8");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCompany(company);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final ClientRequestDto clientRequestDto = new ClientRequestDto();
        clientRequestDto.general(general);
        clientRequestDto.address(address);
        clientRequestDto.invoicing(invoicing);
        clientRequestDto.billingAddress(billingAddress);

        final CreateClientResponseDto expect = new CreateClientResponseDto();
        expect.data(new StandardResponseMessageDto("Client created successfully"));
        // When
        final CreateClientResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(clientRequestDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreateClientResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createClientWithDataDynamicInvalidDtoInGeneral")
    void test_createClient_withRequestValidInvalid_shouldReturnException(final String testName,
                                                                        final TypeLegalIdDto typePersonDto,
                                                                        final String rfc,
                                                                        final String email,
                                                                        final String phone,
                                                                        final String field,
                                                                        final String errorDesc) {

        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA",
                true, null,
                List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null,null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("8");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCompany(company);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final GeneralInfoDto generalInfoDtoDataNull = new GeneralInfoDto();
        generalInfoDtoDataNull.setTypeLegalId(typePersonDto);
        generalInfoDtoDataNull.setRfc(rfc);
        generalInfoDtoDataNull.setBirthdate(LocalDate.parse("1985-08-15"));
        generalInfoDtoDataNull.setEmail(email);
        generalInfoDtoDataNull.setPhoneNumber(phone);
        generalInfoDtoDataNull.setName("alexandra");
        generalInfoDtoDataNull.setSurname("ales");
        generalInfoDtoDataNull.setSecondSurname("secondSurname");
        generalInfoDtoDataNull.setCurp("PGJH010101HNGRLN04");
        final GeneralInfoDto invoicingDto = TestFixturesClient.createInvoicingType2(TypeLegalIdDto.NUMBER_2,
                "EMP241106MG4",
                "empresa 23", "1985-08-15", "Juan", "1985-08-15", "juan.perez@email.com", "5551234567",
                new GeneralInfoTaxReformDto("key", "value"), "12345", GeneralInfoDto.GenderEnum.E);
        final AddressDto addressDto = TestFixturesClient.createAddressDto("Main St", "123", "B", "12345", "Jalisco",
                "Guadalajara", "Downtown");
        final ClientRequestDto clientRequestDto = new ClientRequestDto();
        clientRequestDto.general(generalInfoDtoDataNull);
        clientRequestDto.address(addressDto);
        clientRequestDto.invoicing(invoicingDto);
        clientRequestDto.billingAddress(addressDto);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-INPUT-002");
        errorItem.setDescription(errorDesc);
        errorItem.setField(field);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(clientRequestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createClientWithDataDynamicInvalidDtoInAddress")
    void test_createClient_withRequestValidInvalidInAddress_shouldReturnException(final String testName,
                                                                                 final String street,
                                                                                 final String exteriorNumber,
                                                                                 final String zipCode,
                                                                                 final String state,
                                                                                 final String municipality,
                                                                                 final String neighborhood,
                                                                                 final String field,
                                                                                 final String errorDesc) {

        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA",
                true, null,
                List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null, null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("8");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCompany(company);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final GeneralInfoDto generalInfo = TestFixturesClient.createTypePerson1(TypeLegalIdDto.NUMBER_1,
                "PEGJ851106LN3", "1985-08-15", "juan.perez@email.com", "5551234567", "Perez", "Garcia",
                "Carlos", "carlos", "PGJH010101HNGRLN04", GeneralInfoDto.GenderEnum.M);

        final GeneralInfoDto invoicing = new GeneralInfoDto();
        invoicing.setTypeLegalId(TypeLegalIdDto.NUMBER_1);
        invoicing.setRfc("PEGJ851106LN3");
        invoicing.setBirthdate(LocalDate.parse("1985-08-15"));
        invoicing.setEmail("juan.perez@email.com");
        invoicing.setPhoneNumber("5551234567");
        invoicing.setName("Juan");
        invoicing.setSecondName("Carlos");
        invoicing.setSurname("Perez");
        invoicing.setSecondSurname("Garcia");
        invoicing.setCurp("PGJH010101HNGRLN04");
        invoicing.setGender(GeneralInfoDto.GenderEnum.M);
        final GeneralInfoTaxReformDto taxRegimeDto = new GeneralInfoTaxReformDto("key", "value");
        invoicing.setTaxReform(taxRegimeDto);
        invoicing.setReceiverCode("12345");
        final AddressDto addressDto = TestFixturesClient.createAddressDto("Main St", "123", "B", "12345", "Jalisco",
                "Guadalajara", "Downtown");
        final AddressDto addressDtoError = new AddressDto();
        addressDtoError.setStreetName(street);
        addressDtoError.setStreetNumberExt(exteriorNumber);
        addressDtoError.setZipCode(zipCode);
        addressDtoError.setStateId(state);
        addressDtoError.setMunicipality(municipality);
        addressDtoError.setColonyId(neighborhood);
        final ClientRequestDto clientRequestDto = new ClientRequestDto();
        clientRequestDto.general(generalInfo);
        clientRequestDto.address(addressDtoError);
        clientRequestDto.invoicing(invoicing);
        clientRequestDto.billingAddress(addressDto);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-INPUT-002");
        errorItem.setField(field);
        errorItem.setDescription(errorDesc);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(clientRequestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createClientWithDataDynamicInvalidBusinessRulesType1")
    void test_createClient_withRequestInValid_shouldReturnError(final String testName,
                                                               final String rfc,
                                                               final String firstSurname,
                                                               final String lastName,
                                                               final String legalRepresentativeBirthDate,
                                                               final String curp,
                                                               final String rfc2,
                                                               final String businessName,
                                                               final String constitutionDate,
                                                               final String legalRepresentativeName,
                                                                final String description) {
        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA",
                true, null,
                List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null,null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("8");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCompany(company);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final GeneralInfoDto generalInfoDto = new GeneralInfoDto();
        generalInfoDto.setTypeLegalId(TypeLegalIdDto.NUMBER_1);
        generalInfoDto.setRfc(rfc);
        generalInfoDto.setSecondName("jose");
        generalInfoDto.setSurname(firstSurname);
        generalInfoDto.setSecondSurname(lastName);
        generalInfoDto.setBirthdate(LocalDate.parse(legalRepresentativeBirthDate));
        generalInfoDto.setCurp(curp);
        generalInfoDto.setEmail("juan.perez@email.com");
        generalInfoDto.setPhoneNumber("5551234567");
        final GeneralInfoDto invoicingDto = new GeneralInfoDto();
        invoicingDto.setTypeLegalId(TypeLegalIdDto.NUMBER_2);
        invoicingDto.setRfc(rfc2);
        invoicingDto.setBusinessName(businessName);
        invoicingDto.setConstitutionDate(LocalDate.parse(constitutionDate));
        invoicingDto.setLegalRepresentativeName(legalRepresentativeName);
        invoicingDto.setBirthdate(LocalDate.parse(legalRepresentativeBirthDate));
        invoicingDto.setEmail("juan.perez@email.com");
        invoicingDto.setPhoneNumber("5551234567");
        final GeneralInfoTaxReformDto taxRegime = new GeneralInfoTaxReformDto();
        taxRegime.key("key");
        taxRegime.value("value");
        invoicingDto.setTaxReform(taxRegime);
        invoicingDto.setReceiverCode("54321");
        final AddressDto addressDto = TestFixturesClient.createAddressDto("Main St", "123", "B", "12345", "Jalisco",
                "Guadalajara", "Downtown");
        final ClientRequestDto clientRequestDto = new ClientRequestDto();
        clientRequestDto.general(generalInfoDto);
        clientRequestDto.address(addressDto);
        clientRequestDto.invoicing(invoicingDto);
        clientRequestDto.billingAddress(addressDto);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-CV-007");
        errorItem.setDescription(description);
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(clientRequestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createClient_withRequestValidButWithoutHavingGroupInTheDB_shouldReturnClientDataExceptionAndStatusBadRequest() {
        // Given
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null,null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("97");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCompany(company);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setGroups(null);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final ClientRequestDto clientRequestDto = new ClientRequestDto();
        final GeneralInfoTaxReformDto taxRegimeDto = new GeneralInfoTaxReformDto("key", "value");
        final GeneralInfoDto invoicing = TestFixturesClient.createInvoicingType2(TypeLegalIdDto.NUMBER_2, "EMP241106MG4",
                "empresa 23", "1985-08-15", "Juan", "1985-08-15", "juan.perez@email.com", "5551234567", taxRegimeDto,
                "54321", GeneralInfoDto.GenderEnum.E);
        final AddressDto addressDto = TestFixturesClient.createAddressDto("Main St", "123", "B", "12345", "Jalisco",
                "Guadalajara", "Downtown");
        clientRequestDto.general(invoicing);
        clientRequestDto.address(addressDto);
        clientRequestDto.invoicing(invoicing);
        clientRequestDto.billingAddress(addressDto);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-CV-007");
        errorItem.setDescription("El client no tiene un grupo de coberturas");
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 97)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(clientRequestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createClientWithDataDynamicInvalidDtoInGeneralInFormat")
    void test_createClient_withRequestValidInvalidFormat_shouldReturnClientDataException(final String testName,
                                                                        final String rfc,
                                                                        final String firstName,
                                                                        final String email,
                                                                        final String phone,
                                                                        final String field,
                                                                        final String description,
                                                                        final String firstSurname,
                                                                        final String lastName,
                                                                        final String businessName) {

        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA",
                true, null,
                List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null,null, null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("8");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCompany(company);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final GeneralInfoDto generalInfoDtoDataNull = new GeneralInfoDto();
        generalInfoDtoDataNull.setTypeLegalId(TypeLegalIdDto.NUMBER_1);
        generalInfoDtoDataNull.setRfc(rfc);
        generalInfoDtoDataNull.setBirthdate(LocalDate.parse("2024-11-06"));
        generalInfoDtoDataNull.setEmail(email);
        generalInfoDtoDataNull.setPhoneNumber(phone);
        generalInfoDtoDataNull.setName(firstName);
        generalInfoDtoDataNull.setSurname(firstSurname);
        generalInfoDtoDataNull.setSecondSurname(lastName);
        generalInfoDtoDataNull.setCurp("PGJH010101HNGRLN04");
        final GeneralInfoDto invoicing = TestFixturesClient.createInvoicingType2(TypeLegalIdDto.NUMBER_2, "EMP241106MG4",
                businessName, "2024-11-06", "Juan", "2024-11-06", "juan.perez@email.com", "5551234567",
                new GeneralInfoTaxReformDto("key", "value"), "00005", GeneralInfoDto.GenderEnum.E);

        final AddressDto addressDto = TestFixturesClient.createAddressDto("Main St", "123", "B", "12345", "Jalisco",
                "Guadalajara", "Downtown");
        final ClientRequestDto clientRequestDto = new ClientRequestDto();
        clientRequestDto.general(generalInfoDtoDataNull);
        clientRequestDto.address(addressDto);
        clientRequestDto.invoicing(invoicing);
        clientRequestDto.billingAddress(addressDto);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-INPUT-002");
        errorItem.setDescription(description);
        errorItem.setField(field);
        errorItem.setTraceId(null);
        final List<StandardErrorDto> errors = new ArrayList<>();
        errors.add(errorItem);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(errors);
        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(clientRequestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createClientWithDataDynamicInvalidInGeneralInfoTypeOne")
    void test_createClient_withRequestInValid_shouldReturn(final String testName,
                                                           final String secondSurname,
                                                           final String surname, final String curp,
                                                           final GeneralInfoDto.GenderEnum gender, final String name,
                                                           final String birthdate, final String code) {
        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA",
                true, null,
                List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null,null, null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("58");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCompany(company);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final GeneralInfoDto invoicing = TestFixturesClient.createInvoicingType2(TypeLegalIdDto.NUMBER_2, "EMP241106MG4",
                "empresa 23", "1985-08-15", "Juan", "1985-08-15", "juan.perez@email.com", "5551234567",
                new GeneralInfoTaxReformDto("key", "value"), "12345", GeneralInfoDto.GenderEnum.E);
        final AddressDto addressDto = TestFixturesClient.createAddressDto("Main St", "123", "B", "12345", "Jalisco",
                "Guadalajara", "Downtown");
        final GeneralInfoDto general = new GeneralInfoDto();
        general.setTypeLegalId(TypeLegalIdDto.NUMBER_1);
        general.setRfc("PEGJ851106LN3");
        general.setName(name);
        general.setBirthdate(LocalDate.parse(birthdate));
        general.setPhoneNumber("3145669080");
        general.setEmail("contact@xyzcorporation.com");
        general.setName(name);
        general.setSurname(surname);
        general.setSecondSurname(secondSurname);
        general.setCurp(curp);
        general.setGender(gender);
        final ClientRequestDto clientRequestDto = new ClientRequestDto();
        clientRequestDto.general(general);
        clientRequestDto.address(addressDto);
        clientRequestDto.invoicing(invoicing);
        clientRequestDto.billingAddress(addressDto);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-CV-007");
        errorItem.setDescription(code);
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 58)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(clientRequestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createClient_withRequestInValidInInvoicing_shouldReturnClientDataExceptionStatusBadRequest() {
        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA",
                true, null,
                List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null,null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("58");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCompany(company);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final GeneralInfoDto general = TestFixturesClient.createInvoicingType2(TypeLegalIdDto.NUMBER_2, "EMP241106MG4",
                "empresa", "1985-08-15", "Juan", "1985-11-06", "juan.perez@email.com", "5551234567",
                new GeneralInfoTaxReformDto("key", "value"), "12345", GeneralInfoDto.GenderEnum.E);
        final AddressDto addressDto = TestFixturesClient.createAddressDto("Main St", "123", "B", "12345", "Jalisco",
                "Guadalajara", "Downtown");
        final GeneralInfoDto invoicing = new GeneralInfoDto();
        invoicing.setTypeLegalId(TypeLegalIdDto.NUMBER_1);
        invoicing.setRfc("PEGJ851106LN3");
        invoicing.setBirthdate(LocalDate.parse("1985-11-06"));
        invoicing.setPhoneNumber("3145669080");
        invoicing.setEmail("contact@xyzcorporation.com");
        invoicing.setName("Juan");
        invoicing.setSurname("Perez");
        invoicing.setSecondSurname("Garcia");
        invoicing.setCurp("PEGJ851106HASRRN08");
        invoicing.setGender(GeneralInfoDto.GenderEnum.M);
        final ClientRequestDto clientRequestDto = new ClientRequestDto();
        clientRequestDto.general(general);
        clientRequestDto.address(addressDto);
        clientRequestDto.invoicing(invoicing);
        clientRequestDto.billingAddress(addressDto);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-CV-007");
        errorItem.setDescription("El Régimen Fiscal no puede ser nulo");
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 58)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(clientRequestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createClientWithDataDynamicInvalidBusinessRulesType2")
    void test_createClient_withRequestInValidTypeTwo_shouldReturnError(final String testName,
                                                                final String businessName,
                                                                final LocalDate constitutionDate,
                                                                final String legalRepresentativeName,
                                                                final LocalDate birthdate,
                                                                       final String code) {
        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA",
                true, null,
                List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null,null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("8");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCompany(company);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final GeneralInfoDto generalInfoDto = new GeneralInfoDto();
        generalInfoDto.setTypeLegalId(TypeLegalIdDto.NUMBER_2);
        generalInfoDto.setRfc("EMP241106MG4");
        generalInfoDto.setBusinessName(businessName);
        generalInfoDto.setConstitutionDate(constitutionDate);
        generalInfoDto.setLegalRepresentativeName(legalRepresentativeName);
        generalInfoDto.setBirthdate(birthdate);
        generalInfoDto.setEmail("juan.perez@email.com");
        generalInfoDto.setPhoneNumber("5551234567");
        generalInfoDto.setGender(GeneralInfoDto.GenderEnum.E);
        final GeneralInfoDto invoicingDto = new GeneralInfoDto();
        invoicingDto.setTypeLegalId(TypeLegalIdDto.NUMBER_2);
        invoicingDto.setRfc("EMP241106MG4");
        invoicingDto.setBusinessName("businessName");
        invoicingDto.setConstitutionDate(LocalDate.parse("2024-11-06"));
        invoicingDto.setLegalRepresentativeName("legalRepresentativeName");
        invoicingDto.setBirthdate(LocalDate.parse("2024-11-06"));
        invoicingDto.setEmail("juan.perez@email.com");
        invoicingDto.setPhoneNumber("5551234567");
        invoicingDto.setGender(GeneralInfoDto.GenderEnum.E);
        final GeneralInfoTaxReformDto taxRegime = new GeneralInfoTaxReformDto();
        taxRegime.key("key");
        taxRegime.value("value");
        invoicingDto.setTaxReform(taxRegime);
        invoicingDto.setReceiverCode("54321");
        final AddressDto addressDto = TestFixturesClient.createAddressDto("Main St", "123", "B", "12345", "Jalisco",
                "Guadalajara", "Downtown");
        final ClientRequestDto clientRequestDto = new ClientRequestDto();
        clientRequestDto.general(generalInfoDto);
        clientRequestDto.address(addressDto);
        clientRequestDto.invoicing(invoicingDto);
        clientRequestDto.billingAddress(addressDto);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-CV-007");
        errorItem.setDescription(code);
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(clientRequestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

    @Test
    void test_createClient_withRequestInValidGenderTypeTwo_shouldReturnErrorCode007AnsStatusBadRequest() {
        // Given
        final YearLimit max = new YearLimit(12, "YEAR");
        final YearLimit min = new YearLimit(12, "YEAR");
        final AgeLimit acceptableYearOldLimit = new AgeLimit(min, max);
        final AgeLimit renovationYearOldLimit = new AgeLimit(min, max);
        final AgeLimit cancellationYearOldLimit = new AgeLimit(min, max);
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(new Sum(new BigDecimal(500000), null, null, null), new Sum(new BigDecimal(500000),
                null, null, null));
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, acceptableYearOldLimit,
                renovationYearOldLimit, cancellationYearOldLimit, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento", "BASICA",
                true, null,
                List.of(insuredValidation), null, null, null, null);
        final List<Salary> salaries = List.of(new Salary("Asegurado 1", 15000.0, "ADMINISTRATIVOS"), new Salary("Asegurado 2", 18000.0, "OPERATIVOS"), new Salary("Asegurado 3", 20000.0, "ADMINISTRATIVOS"));

        final GroupVg groupVg1 = new GroupVg(3, "estandar", "SAFIJA", 12, 12, new BigDecimal(12), new BigDecimal(12), new BigDecimal("12.12"), 12, null,
                salaries, List.of(coverageDetail));
        final List<GroupVg> groupVgs = new ArrayList<>();
        groupVgs.add(groupVg1);
        final Insured insured = new Insured(12, 12, null);
        final Age age = new Age(12, 12, 12, 12, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final ModalityValidation modalityValidation = new ModalityValidation(insured, age, limitBasic, 3, null, null,null, null, null, null, null);
        final FolioNumber folioNumber = new FolioNumber("8");
        final CatalogItem catalogItem = new CatalogItem("key", "value");
        final Company company = new Company("company", catalogItem, 12, 12, 12, new BigDecimal(12), new BigDecimal(12));
        final RecordFolio recordFolio = RecordFolio.builder()
                .userId("John Doe")
                .pointOfSaleId("POINT")
                .groupId("office001")
                .subgroupId("Main Office")
                .rateProfileId("agent001")
                .name("Jane Doe")
                .email("email")
                .officeId("office001")
                .officeDescription("Main Office")
                .agentId("agent001")
                .agentName("Jane Doe")
                .promoterName("Promoter Inc.")
                .promoterId("promoter001")
                .build();
        final FolioRecordEntity folioRecord = new FolioRecordEntity();
        folioRecord.setId(folioNumber);
        folioRecord.setAgentData(recordFolio);
        folioRecord.setModality("TRADICIONAL");
        folioRecord.setCompany(company);
        folioRecord.setModalityValidation(modalityValidation);
        folioRecord.setGroups(groupVgs);
        folioRecord.setCreatedAt(LocalDateTime.parse("2024-07-20T15:31:11.141"));
        folioRecord.setUpdatedAt(LocalDateTime.parse("2024-09-20T15:31:11.141"));
        folioRecord.setStatus("Abierto");
        this.reactiveMongoTemplate.insert(folioRecord).block();
        final GeneralInfoDto generalInfoDto = new GeneralInfoDto();
        generalInfoDto.setTypeLegalId(TypeLegalIdDto.NUMBER_2);
        generalInfoDto.setRfc("EMP241106MG4");
        generalInfoDto.setBusinessName("businessName");
        generalInfoDto.setConstitutionDate(LocalDate.parse("2024-11-06"));
        generalInfoDto.setLegalRepresentativeName("legalRepresentativeName");
        generalInfoDto.setBirthdate(LocalDate.parse("2024-11-06"));
        generalInfoDto.setEmail("juan.perez@email.com");
        generalInfoDto.setPhoneNumber("5551234567");
        final GeneralInfoDto invoicingDto = new GeneralInfoDto();
        invoicingDto.setTypeLegalId(TypeLegalIdDto.NUMBER_2);
        invoicingDto.setRfc("EMP241106MG4");
        invoicingDto.setBusinessName("businessName");
        invoicingDto.setConstitutionDate(LocalDate.parse("2024-11-06"));
        invoicingDto.setLegalRepresentativeName("legalRepresentativeName");
        invoicingDto.setBirthdate(LocalDate.parse("2024-11-06"));
        invoicingDto.setEmail("juan.perez@email.com");
        invoicingDto.setPhoneNumber("5551234567");
        invoicingDto.setGender(GeneralInfoDto.GenderEnum.M);
        final GeneralInfoTaxReformDto taxRegime = new GeneralInfoTaxReformDto();
        taxRegime.key("key");
        taxRegime.value("value");
        invoicingDto.setTaxReform(taxRegime);
        invoicingDto.setReceiverCode("54321");
        final AddressDto addressDto = TestFixturesClient.createAddressDto("Main St", "123", "B", "12345", "Jalisco",
                "Guadalajara", "Downtown");
        final ClientRequestDto clientRequestDto = new ClientRequestDto();
        clientRequestDto.general(generalInfoDto);
        clientRequestDto.address(addressDto);
        clientRequestDto.invoicing(invoicingDto);
        clientRequestDto.billingAddress(addressDto);

        final StandardErrorDto errorItem = new StandardErrorDto();
        errorItem.setCode("VG-CV-007");
        errorItem.setDescription("El género no puede ser nulo");
        errorItem.setField(null);
        final StandardErrorResponseDto expect = new StandardErrorResponseDto();
        expect.setErrors(List.of(errorItem));

        // When
        final StandardErrorResponseDto actualResponse = this.webTestClient.post()
                .uri(BASE_PATH, 8)
                .headers(headers -> headers.setBasicAuth(USERNAME, PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(clientRequestDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(StandardErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Then
        assertEquals(expect, actualResponse);
    }

}
