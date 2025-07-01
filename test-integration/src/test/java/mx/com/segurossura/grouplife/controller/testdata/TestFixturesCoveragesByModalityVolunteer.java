package mx.com.segurossura.grouplife.controller.testdata;

import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.AgeDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.AgeLimitDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.CatalogDataDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.ComparatorDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.CoverageCatalogDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.CoverageDetailDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.DependenciesDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.DiffAdminOpDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuranceDataDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuredDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuredSumLimitDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuredValidationDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.LimitBasic;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.SumDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.YearLimitDto;
import mx.com.segurossura.grouplife.openapi.model.CoverageDetailInsuredSumCoveragesInnerDto;
import mx.com.segurossura.grouplife.openapi.model.GroupVgResponseDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredSumCoveragesCoveragesInformationInnerDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredSumCoveragesDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestFixturesCoveragesByModalityVolunteer {
    public static InsuranceDataDto createInsuranceDataDto() {
        final List<CatalogDataDto> catalogDataList = new ArrayList<>();
        catalogDataList.add(createCatalogDataDto());

        return new InsuranceDataDto(catalogDataList);
    }

    public static CatalogDataDto createCatalogDataDto() {
        final List<CoverageCatalogDto> coverageCatalogList = new ArrayList<>();
        final CoverageCatalogDto coverage1 = createCoverageSCatalogDtoToVolunteer();
        coverageCatalogList.add(coverage1);
        return new CatalogDataDto("Product Description", "Product", coverageCatalogList);
    }

    public static CoverageCatalogDto createCoverageSCatalogDtoToVolunteer() {
        final CoverageDetailDto coverages1 = coverageDetail98430Dto();
        final CoverageDetailDto coverages2 = coverageDetail98420Dto();
        final CoverageDetailDto coverages3 = coverageDetail98560Dto();
        final List<CoverageDetailDto> coverages = new ArrayList<>();
        coverages.add(coverages1);
        coverages.add(coverages2);
//        coverages.add(coverages3);
        return new CoverageCatalogDto("VOLUNTARIA", "Coverage Description", insuredDto(), ageDto(), coverages, 3,
                new LimitBasic(43, 25)
                ,null, null,null, new ComparatorDto(25, "comparator"), null, null, null);
    }

    public static CoverageDetailDto coverageDetail98430Dto() {
        return new CoverageDetailDto("MUERTE_ACCIDENTAL", "9843", "Muerte accidental",
                "OPCIONAL", null, false, createInsureValidationDto98430(), null, null, false);
    }

    public static CoverageDetailDto coverageDetail98560Dto() {
        return new CoverageDetailDto("ASISTENCIA_PLUS", "98560", "Asistencia familiar",
                "OPCIONAL", null, false, createInsureValidationDto98560(), null, null, false);
    }

    public static CoverageDetailDto coverageDetail98420Dto() {
        return new CoverageDetailDto("FALLECIMIENTO", "9842", "Fallecimiento",
                "BASICA", null, true, createInsureValidationDto98420(), null, null, false);
    }

    public static List<InsuredValidationDto> createInsureValidationDto98430() {
        final List<InsuredValidationDto> coveragesVolunteer = new ArrayList<>();
        final InsuredValidationDto validationT = new InsuredValidationDto(null, "T", "Titular", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98430T(), createSumMax98430T()));
        final InsuredValidationDto validationC = new InsuredValidationDto(null, "C", "Conyuge", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98430(), createSumMax98430C()));
        final InsuredValidationDto validationH = new InsuredValidationDto(null, "H", "Hijos", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98430(), createSumMax98430C()));
        final InsuredValidationDto validationR = new InsuredValidationDto(null, "D", "Hermanos", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98430(), createSumMax98430C()));
        final InsuredValidationDto validationP = new InsuredValidationDto(null, "A", "Padres", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98430(), createSumMax98430C()));
        coveragesVolunteer.add(validationT);
        coveragesVolunteer.add(validationC);
        coveragesVolunteer.add(validationH);
        coveragesVolunteer.add(validationR);
        coveragesVolunteer.add(validationP);
        return coveragesVolunteer;
    }

    public static List<InsuredValidationDto> createInsureValidationDto98560() {
        final List<InsuredValidationDto> coveragesVolunteer = new ArrayList<>();
        final InsuredValidationDto validationT = new InsuredValidationDto(null, "T", "Titular", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98420T(), createSumMax98420T()));
        final InsuredValidationDto validationC = new InsuredValidationDto(null, "C", "Conyuge", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98420(), createSumMax98420()));
        final InsuredValidationDto validationH = new InsuredValidationDto(null, "H", "Hijos", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98420(), createSumMax98420()));
        final InsuredValidationDto validationD = new InsuredValidationDto(null, "D", "Hermanos", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98420(), createSumMax98420()));
        final InsuredValidationDto validationA = new InsuredValidationDto(null, "A", "Padres", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98420(), createSumMax98420()));
        coveragesVolunteer.add(validationT);
        coveragesVolunteer.add(validationC);
        coveragesVolunteer.add(validationH);
        coveragesVolunteer.add(validationD);
        coveragesVolunteer.add(validationA);
        return coveragesVolunteer;
    }

    public static List<InsuredValidationDto> createInsureValidationDto98420() {
        final List<InsuredValidationDto> coveragesVolunteer = new ArrayList<>();
        final InsuredValidationDto validationT = new InsuredValidationDto(null, "T", "Titular", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98420T(), createSumMax98420T()));
        final InsuredValidationDto validationC = new InsuredValidationDto(null, "C", "Conyuge", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98420(), createSumMax98420()));
        final InsuredValidationDto validationH = new InsuredValidationDto(null, "H", "Hijos", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98420(), createSumMax98420()));
        final InsuredValidationDto validationD = new InsuredValidationDto(null, "D", "Hermanos", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98420(), createSumMax98420()));
        final InsuredValidationDto validationA = new InsuredValidationDto(null, "A", "Padres", createAgeLimitDto(), null,
                null, insuredSumLimitVolunteerDto(createSumMin98420(), createSumMax98420()));
        coveragesVolunteer.add(validationT);
        coveragesVolunteer.add(validationC);
        coveragesVolunteer.add(validationH);
        coveragesVolunteer.add(validationD);
        coveragesVolunteer.add(validationA);
        return coveragesVolunteer;
    }

    public static SumDto createSumMin98430C() {
        return new SumDto(null, createDependencies("9842", "T"),  "20% de la Suma asegurada del titular", "EQ({dependencies})*0.2");
    }

    public static SumDto createSumMin98420() {
        return new SumDto(null, createDependencies("9842", "T"),  "20% de la Suma asegurada del titular", "EQ({dependencies})*0.2");
    }

    public static SumDto createSumMax98430C() {
        return new SumDto(null, createDependencies("9843", "T"),  "50% de la Suma asegurada del titular",  "EQ" +
                "({dependencies})*0.5");
    }

    public static SumDto createSumMax98420() {
        return new SumDto(null, createDependencies("9842", "T"),  "50% de la Suma asegurada del titular",  "EQ({dependencies})*0.5");
    }

    public static SumDto createSumMin98430T() {
        return new SumDto( new BigDecimal(50000), createDependencies("9842", "T"),
                "20% de la Suma asegurada del titular",  "EQ({dependencies})*0.2");
    }

    public static SumDto createSumMin9856T() {
        return new SumDto( new BigDecimal(50000), createDependencies("9842", "T"),
                "20% de la Suma asegurada del titular",  "EQ({dependencies})<=0.2");
    }

    public static SumDto createSumMin98430() {
        return new SumDto( null, createDependencies("9843", "T"),
                "20% de la Suma asegurada del titular",  "EQ({dependencies})*0.2");
    }

    public static SumDto createSumMin98420T() {
        return new SumDto(new BigDecimal(50000), null, null, null);
    }

    public static SumDto createSumMax98430T() {
        return new SumDto(new BigDecimal(500000), createDependencies("9842", "T"),
                "La suma asegurada de la cobertura Indemnización diaria por hospitalización" +
                        " (Periodo de espera 0 días) debe ser menor o igual a la suma asegurada de la cobertura 9842.",
                "({InsuredSum} <= dependencies)");
    }

    public static SumDto createSumMax98420T() {
        return new SumDto(new BigDecimal(500000), null, null, null);
    }

    public static List<InsuredSumLimitDto> insuredSumLimitVolunteerDto(final SumDto min, final SumDto max) {
        final InsuredSumLimitDto insuredSumLimit1 = new InsuredSumLimitDto(min, max);
        final List<InsuredSumLimitDto> result = new ArrayList<>();
        result.add(insuredSumLimit1);
        return result;
    }

    public static DependenciesDto createDependencies(final String coverageKey, final String kinshipKey) {
        return new DependenciesDto(coverageKey, kinshipKey);
    }

    public static CoverageDetailDto coverageDetail1Dto() {
        return new CoverageDetailDto("FALLECIMIENTO", "9842", "Fallecimiento",
                "BASICA", null, true, List.of(createInsureValidation1Dto()), null, null, false);

    }

    public static InsuredValidationDto createInsureValidation1Dto() {
        return new InsuredValidationDto(null, "T", "Titular", createAgeLimitDto(),
                createAgeLimitDto(), createAgeLimitDto(), insuredSumLimit1Dtos());
    }

    public static List<InsuredSumLimitDto> insuredSumLimit1Dtos() {
        final InsuredSumLimitDto insuredSumLimit1 = new InsuredSumLimitDto(create1SumMin(), create1SumMax());
        final List<InsuredSumLimitDto> result = new ArrayList<>();
        result.add(insuredSumLimit1);
        return result;
    }

    public static SumDto create1SumMin() {
        return new SumDto(new BigDecimal(50000), null, null, null);
    }

    public static SumDto create1SumMax() {
        return new SumDto(new BigDecimal(5000000), null, null, null);
    }

    public static AgeLimitDto createAgeLimitDto() {
        return new AgeLimitDto(createYearLimit1DtoMin(), createYearLimit1DtoMax());
    }

    public static YearLimitDto createYearLimit1DtoMin() {
        return new YearLimitDto(69, "YEAR");

    }

    public static YearLimitDto createYearLimit1DtoMax() {
        return new YearLimitDto(60, "YEAR");

    }

    public static AgeDto ageDto() {
        return new AgeDto(12, 65, 24, 60, null);

    }

    public static InsuredDto insuredDto() {
        return new InsuredDto(7, 1000, diffAdminOpDto());

    }

    public static DiffAdminOpDto diffAdminOpDto() {
        return new DiffAdminOpDto(30, "comparator");

    }

    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesDtoVolunteerError() {
        final List<InsuredSumCoveragesDto> coverages = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto.setCode("9842");
        insuredSumCoveragesDto.setCoveragesInformation(createInsureInformationDto());
        coverages.add(insuredSumCoveragesDto);
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(createInsureInformationDtoError());
        coverages.add(insuredSumCoveragesDto2);
        return coverages;
    }

    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesDtoVolunteerErrorInSumsIn98430() {
        final List<InsuredSumCoveragesDto> coverages = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto.setCode("9842");
        insuredSumCoveragesDto.setCoveragesInformation(createInsureInformationDto());
        coverages.add(insuredSumCoveragesDto);
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(createInsureInformationDto98430());
        coverages.add(insuredSumCoveragesDto2);
        return coverages;
    }
    public static List<InsuredSumCoveragesCoveragesInformationInnerDto> createInsureInformationDto98430() {
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> result = new ArrayList<>();
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesT =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesT.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.T);
        insuredSumCoveragesT.setInsuredSum(new BigDecimal(200000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesC =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesC.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.C);
        insuredSumCoveragesC.setInsuredSum(null);
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesH =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesH.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.H);
        insuredSumCoveragesH.setInsuredSum(new BigDecimal(90000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesD =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesD.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.D);
        insuredSumCoveragesD.setInsuredSum(new BigDecimal(80000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesA =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesA.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.A);
        insuredSumCoveragesA.setInsuredSum(new BigDecimal(80000));
        result.add(insuredSumCoveragesT);
        result.add(insuredSumCoveragesC);
        result.add(insuredSumCoveragesH);
        result.add(insuredSumCoveragesD);
        result.add(insuredSumCoveragesA);
        return result;
    }

    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesDtoVolunteerErrorInSums() {
        final List<InsuredSumCoveragesDto> coverages = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto.setCode("9842");
        insuredSumCoveragesDto.setCoveragesInformation(createInsureInformationDto());
        coverages.add(insuredSumCoveragesDto);
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(createInsureInformationDtoErrorInsuredsSum());
        coverages.add(insuredSumCoveragesDto2);
        return coverages;
    }

    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesDtoVolunteer() {
        final List<InsuredSumCoveragesDto> coverages = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto.setCode("9842");
        insuredSumCoveragesDto.setCoveragesInformation(createInsureInformationDto());
        coverages.add(insuredSumCoveragesDto);
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(createInsureInformationDto());
        coverages.add(insuredSumCoveragesDto2);
        return coverages;
    }

    public static List<InsuredSumCoveragesCoveragesInformationInnerDto> createInsureInformationDtoErrorInsuredsSum() {
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> result = new ArrayList<>();
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesT =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesT.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.T);
        insuredSumCoveragesT.setInsuredSum(new BigDecimal(200000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesC =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesC.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.C);
        insuredSumCoveragesC.setInsuredSum(new BigDecimal(100000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesH =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesH.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.H);
        insuredSumCoveragesH.setInsuredSum(new BigDecimal(900000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesD =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesD.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.D);
        insuredSumCoveragesD.setInsuredSum(new BigDecimal(80000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesA =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesA.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.A);
        insuredSumCoveragesA.setInsuredSum(new BigDecimal(80000));
        result.add(insuredSumCoveragesT);
        result.add(insuredSumCoveragesC);
        result.add(insuredSumCoveragesH);
        result.add(insuredSumCoveragesD);
        result.add(insuredSumCoveragesA);
        return result;
    }

    public static List<InsuredSumCoveragesCoveragesInformationInnerDto> createInsureInformationDtoError() {
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> result = new ArrayList<>();
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesT =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesT.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.T);
        insuredSumCoveragesT.setInsuredSum(new BigDecimal(200000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesC =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesC.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.C);
        insuredSumCoveragesC.setInsuredSum(new BigDecimal(100000));
        result.add(insuredSumCoveragesT);
        result.add(insuredSumCoveragesC);
        return result;
    }

    public static List<InsuredSumCoveragesCoveragesInformationInnerDto> createInsureInformationDto() {
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> result = new ArrayList<>();
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesT =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesT.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.T);
        insuredSumCoveragesT.setInsuredSum(new BigDecimal(200000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesC =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesC.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.C);
        insuredSumCoveragesC.setInsuredSum(new BigDecimal(100000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesH =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesH.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.H);
        insuredSumCoveragesH.setInsuredSum(new BigDecimal(90000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesD =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesD.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.D);
        insuredSumCoveragesD.setInsuredSum(new BigDecimal(80000));
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesA =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesA.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.A);
        insuredSumCoveragesA.setInsuredSum(new BigDecimal(80000));
        result.add(insuredSumCoveragesT);
        result.add(insuredSumCoveragesC);
        result.add(insuredSumCoveragesH);
        result.add(insuredSumCoveragesD);
        result.add(insuredSumCoveragesA);
        return result;
    }

    public static GroupVgResponseDto createGroupVgResponseDto() {
        final GroupVgResponseDto response = new GroupVgResponseDto();
        response.setGroupNumber(1);
        response.setName("nameGroupVolunteer");
        response.numAdministrativeInsured(0);
        response.numOperationalInsured(0);
        response.administrativeInsuredSum(null);
        response.operationalInsuredSum(null);
        response.setCoverages(createCoverageDetailDto());
        return response;
    }

    public static List<mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto> createCoverageDetailDto() {
        final List<mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto> result = new ArrayList<>();
        final mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto coverage2 = new mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto();
        coverage2.coverageKey("MUERTE_ACCIDENTAL");
        coverage2.code("9843");
        coverage2.description("Muerte accidental");
        coverage2.setTypeCoverage("OPCIONAL");
        coverage2.setMandatory(false);
        coverage2.setDefaultValue(null);
        coverage2.setInsuredValidations(insuredValidations98430());
        coverage2.setInsuredSumCoverages(createInsuresInformationResultDto());
        coverage2.setInsuredSumFix(false);

        final mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto coverage1 = new mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto();
        coverage1.coverageKey("FALLECIMIENTO");
        coverage1.code("9842");
        coverage1.description("Fallecimiento");
        coverage1.setTypeCoverage("BASICA");
        coverage1.setMandatory(true);
        coverage1.setDefaultValue(null);
        coverage1.setInsuredValidations(insuredValidations98420());
        coverage1.setInsuredSumCoverages(createInsuresInformationResultDto());
        coverage1.setInsuredSumFix(false);

        result.add(coverage2);
        result.add(coverage1);
        return result;
    }

        public static List<mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto> insuredValidations98420() {
            final List<mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto> result = new ArrayList<>();
            final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDto = new mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto();
            insuredValidationDto.kinshipKey("T");
            insuredValidationDto.kinship("Titular");
            insuredValidationDto.acceptableYearOldLimit(createAcceptable());
            insuredValidationDto.insuredSumLimit(List.of(createInsuredLimit98420()));

            final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDtoC =
                    new mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto();
            insuredValidationDtoC.kinshipKey("C");
            insuredValidationDtoC.kinship("Conyuge");
            insuredValidationDtoC.acceptableYearOldLimit(createAcceptable());
            insuredValidationDtoC.insuredSumLimit(List.of(createInsuredLimit98420C()));

            final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDtoH =
                    new mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto();
            insuredValidationDtoH.kinshipKey("H");
            insuredValidationDtoH.kinship("Hijos");
            insuredValidationDtoH.acceptableYearOldLimit(createAcceptable());
            insuredValidationDtoH.insuredSumLimit(List.of(createInsuredLimit98420C()));

            final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDtoD =
                    new mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto();
            insuredValidationDtoD.kinshipKey("D");
            insuredValidationDtoD.kinship("Hermanos");
            insuredValidationDtoD.acceptableYearOldLimit(createAcceptable());
            insuredValidationDtoD.insuredSumLimit(List.of(createInsuredLimit98420C()));

            final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDtoA =
                    new mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto();
            insuredValidationDtoA.kinshipKey("A");
            insuredValidationDtoA.kinship("Padres");
            insuredValidationDtoA.acceptableYearOldLimit(createAcceptable());
            insuredValidationDtoA.insuredSumLimit(List.of(createInsuredLimit98420C()));
            result.add(insuredValidationDto);
            result.add(insuredValidationDtoC);
            result.add(insuredValidationDtoH);
            result.add(insuredValidationDtoD);
            result.add(insuredValidationDtoA);
            return result;
        }

    public static List<mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto> insuredValidations98430() {
        final List<mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto> result = new ArrayList<>();
        final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDto = new mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto();
        insuredValidationDto.kinshipKey("T");
        insuredValidationDto.kinship("Titular");
        insuredValidationDto.acceptableYearOldLimit(createAcceptable());
        insuredValidationDto.insuredSumLimit(List.of(createInsuredLimit98430()));

        final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDtoC =
                new mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto();
        insuredValidationDtoC.kinshipKey("C");
        insuredValidationDtoC.kinship("Conyuge");
        insuredValidationDtoC.acceptableYearOldLimit(createAcceptable());
        insuredValidationDtoC.insuredSumLimit(List.of(createInsuredLimit98430C()));

        final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDtoH =
                new mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto();
        insuredValidationDtoH.kinshipKey("H");
        insuredValidationDtoH.kinship("Hijos");
        insuredValidationDtoH.acceptableYearOldLimit(createAcceptable());
        insuredValidationDtoH.insuredSumLimit(List.of(createInsuredLimit98430C()));

        final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDtoD =
                new mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto();
        insuredValidationDtoD.kinshipKey("D");
        insuredValidationDtoD.kinship("Hermanos");
        insuredValidationDtoD.acceptableYearOldLimit(createAcceptable());
        insuredValidationDtoD.insuredSumLimit(List.of(createInsuredLimit98430C()));

        final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDtoA =
                new mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto();
        insuredValidationDtoA.kinshipKey("A");
        insuredValidationDtoA.kinship("Padres");
        insuredValidationDtoA.acceptableYearOldLimit(createAcceptable());
        insuredValidationDtoA.insuredSumLimit(List.of(createInsuredLimit98430C()));
        result.add(insuredValidationDto);
        result.add(insuredValidationDtoC);
        result.add(insuredValidationDtoH);
        result.add(insuredValidationDtoD);
        result.add(insuredValidationDtoA);
        return result;
    }

    public static List<mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto> insuredValidations() {
        final List<mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto> result = new ArrayList<>();
        final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDto = new mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto();
        insuredValidationDto.kinshipKey("T");
        insuredValidationDto.kinship("Titular");
        insuredValidationDto.acceptableYearOldLimit(createAcceptable());
        insuredValidationDto.insuredSumLimit(List.of(createInsuredLimit()));
        result.add(insuredValidationDto);
        return result;
    }

    public static mx.com.segurossura.grouplife.openapi.model.AgeLimitDto createAcceptable(){
        final mx.com.segurossura.grouplife.openapi.model.AgeLimitDto ageLimitDto =
                new mx.com.segurossura.grouplife.openapi.model.AgeLimitDto();
        ageLimitDto.setMax(createYearLimitDto("YEAR", 60));
        ageLimitDto.setMin(createYearLimitDto("YEAR", 69));
        return ageLimitDto;
    }

    public static mx.com.segurossura.grouplife.openapi.model.YearLimitDto createYearLimitDto(final String unit,
                                                                                    final Integer value) {
        final mx.com.segurossura.grouplife.openapi.model.YearLimitDto yearLimitDto =
                new mx.com.segurossura.grouplife.openapi.model.YearLimitDto();
        yearLimitDto.setUnit(unit);
        yearLimitDto.setValue(value);
        return yearLimitDto;
    }

    public static mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto createInsuredLimit98420() {
        final mx.com.segurossura.grouplife.openapi.model.SumDto min =
                new mx.com.segurossura.grouplife.openapi.model.SumDto();
        min.setDefaultValue(new BigDecimal(50000));
        min.setDependencies(null);
        min.setFormulaDescription(null);
        min.setFormula(null);
        final mx.com.segurossura.grouplife.openapi.model.SumDto max =
                new mx.com.segurossura.grouplife.openapi.model.SumDto();
        max.setDefaultValue(new BigDecimal(500000));
        max.setDependencies(null);
        max.setFormulaDescription(null);
        max.setFormula(null);
        final mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto insuredSumLimitDto =
                new mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto();
        insuredSumLimitDto.max(max);
        insuredSumLimitDto.min(min);
        return insuredSumLimitDto;
    }

    public static mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto createInsuredLimit98420C() {
        final mx.com.segurossura.grouplife.openapi.model.SumDto min =
                new mx.com.segurossura.grouplife.openapi.model.SumDto();
        min.setDefaultValue(null);
        min.setDependencies(createDependenciesResponse("9842", "T"));
        min.setFormulaDescription("20% de la Suma asegurada del titular");
        min.setFormula("EQ({dependencies})*0.2");
        final mx.com.segurossura.grouplife.openapi.model.SumDto max =
                new mx.com.segurossura.grouplife.openapi.model.SumDto();
        max.setDefaultValue(null);
        max.setDependencies(createDependenciesResponse("9842", "T"));
        max.setFormulaDescription("50% de la Suma asegurada del titular");
        max.setFormula("EQ({dependencies})*0.5");
        final mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto insuredSumLimitDto =
                new mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto();
        insuredSumLimitDto.max(max);
        insuredSumLimitDto.min(min);
        return insuredSumLimitDto;
    }


    public static mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto createInsuredLimit98430C() {
        final mx.com.segurossura.grouplife.openapi.model.SumDto min =
                new mx.com.segurossura.grouplife.openapi.model.SumDto();
        min.setDefaultValue(null);
        min.setDependencies(createDependenciesResponse("9843", "T"));
        min.setFormulaDescription("20% de la Suma asegurada del titular");
        min.setFormula("EQ({dependencies})*0.2");
        final mx.com.segurossura.grouplife.openapi.model.SumDto max =
                new mx.com.segurossura.grouplife.openapi.model.SumDto();
        max.setDefaultValue(null);
        max.setDependencies(createDependenciesResponse("9843", "T"));
        max.setFormulaDescription("50% de la Suma asegurada del titular");
        max.setFormula("EQ({dependencies})*0.5");
        final mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto insuredSumLimitDto =
                new mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto();
        insuredSumLimitDto.max(max);
        insuredSumLimitDto.min(min);
        return insuredSumLimitDto;
    }

    public static mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto createInsuredLimit98430() {
        final mx.com.segurossura.grouplife.openapi.model.SumDto min =
                new mx.com.segurossura.grouplife.openapi.model.SumDto();
        min.setDefaultValue(new BigDecimal(50000));
        min.setDependencies(createDependenciesResponse("9842", "T"));
        min.setFormulaDescription("20% de la Suma asegurada del titular");
        min.setFormula("EQ({dependencies})*0.2");
        final mx.com.segurossura.grouplife.openapi.model.SumDto max =
                new mx.com.segurossura.grouplife.openapi.model.SumDto();
        max.setDefaultValue(new BigDecimal(500000));
        max.setDependencies(createDependenciesResponse("9842", "T"));
        max.setFormulaDescription("La suma asegurada de la cobertura Indemnización diaria por hospitalización (Periodo de espera 0 " +
                "días)" +
                " debe ser menor o igual a la suma asegurada de la cobertura 9842.");
        max.setFormula("({InsuredSum} <= dependencies)");
        final mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto insuredSumLimitDto =
                new mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto();
        insuredSumLimitDto.max(max);
        insuredSumLimitDto.min(min);
        return insuredSumLimitDto;
    }

    public static mx.com.segurossura.grouplife.openapi.model.DependenciesDto createDependenciesResponse(final String coverageKey,
                                                                                                final String kinshipKey) {
        final mx.com.segurossura.grouplife.openapi.model.DependenciesDto dependenciesDto =
                new mx.com.segurossura.grouplife.openapi.model.DependenciesDto();
        dependenciesDto.setCoverageKey(coverageKey);
        dependenciesDto.setKinshipKey(kinshipKey);
        return dependenciesDto;
    }

    public static mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto createInsuredLimit() {
        final mx.com.segurossura.grouplife.openapi.model.SumDto min =
                new mx.com.segurossura.grouplife.openapi.model.SumDto();
        min.setDefaultValue(new BigDecimal(50000));
        min.setDependencies(null);
        min.setFormula(null);
        min.formulaDescription(null);
        final mx.com.segurossura.grouplife.openapi.model.SumDto max =
                new mx.com.segurossura.grouplife.openapi.model.SumDto();
        max.setDefaultValue(new BigDecimal(500000));
        max.setDependencies(null);
        max.setFormula(null);
        max.formulaDescription(null);
        final mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto insuredSumLimitDto =
                new mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto();
        insuredSumLimitDto.max(max);
        insuredSumLimitDto.min(min);
        return insuredSumLimitDto;
    }

    public static List<CoverageDetailInsuredSumCoveragesInnerDto> createInsuresInformationResultDto() {
        final List<CoverageDetailInsuredSumCoveragesInnerDto> result = new ArrayList<>();
        final CoverageDetailInsuredSumCoveragesInnerDto insuredSumCoveragesT =
                new CoverageDetailInsuredSumCoveragesInnerDto();
        insuredSumCoveragesT.setInsuredSum(new BigDecimal(200000));
        insuredSumCoveragesT.setType("T");
        final CoverageDetailInsuredSumCoveragesInnerDto insuredSumCoveragesC =
                new CoverageDetailInsuredSumCoveragesInnerDto();
        insuredSumCoveragesC.setInsuredSum(new BigDecimal(100000));
        insuredSumCoveragesC.setType("C");
        final CoverageDetailInsuredSumCoveragesInnerDto insuredSumCoveragesH =
                new CoverageDetailInsuredSumCoveragesInnerDto();
        insuredSumCoveragesH.setInsuredSum(new BigDecimal(90000));
        insuredSumCoveragesH.setType("H");
        final CoverageDetailInsuredSumCoveragesInnerDto insuredSumCoveragesD =
                new CoverageDetailInsuredSumCoveragesInnerDto();
        insuredSumCoveragesD.setInsuredSum(new BigDecimal(80000));
        insuredSumCoveragesD.setType("D");
        final CoverageDetailInsuredSumCoveragesInnerDto insuredSumCoveragesA =
                new CoverageDetailInsuredSumCoveragesInnerDto();
        insuredSumCoveragesA.setInsuredSum(new BigDecimal(80000));
        insuredSumCoveragesA.setType("A");
        result.add(insuredSumCoveragesT);
        result.add(insuredSumCoveragesC);
        result.add(insuredSumCoveragesH);
        result.add(insuredSumCoveragesD);
        result.add(insuredSumCoveragesA);
        return result;
    }
}
