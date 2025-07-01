package mx.com.segurossura.grouplife.controller;

import mx.com.segurossura.grouplife.BaseIT;
import mx.com.segurossura.grouplife.controller.testdata.TestFixtures;
import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.AgeLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageCatalog;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageDetail;
import mx.com.segurossura.grouplife.domain.model.coverage.DiffAdminOp;
import mx.com.segurossura.grouplife.domain.model.coverage.Insured;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredSumLimit;
import mx.com.segurossura.grouplife.domain.model.coverage.InsuredValidation;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.domain.model.coverage.YearLimit;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.AgeDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.AgeLimitDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.CatalogDataDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.CoverageCatalogDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.CoverageDetailDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.DefaultValueDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.DependenciesDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.DiffAdminOpDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuranceDataDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuredDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.InsuredValidationDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages.YearLimitDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CompanyDtoTest extends BaseIT {
    @Mock
    private CatalogMapper catalogMapper;

    @Test
    void testInsuredValidationDtoToBuilder() {
        final InsuredValidationDto insuredValidation = InsuredValidationDto.builder()
                .kinshipKey("child")
                .kinship("Child")
                .build();

        final InsuredValidationDto modifiedValidation = InsuredValidationDto.builder()
                .kinshipKey("parent")
                .kinship("Parent")
                .build();
        assertEquals("child", insuredValidation.kinshipKey());
        assertEquals("Child", insuredValidation.kinship());

        assertEquals("parent", modifiedValidation.kinshipKey());
        assertEquals("Parent", modifiedValidation.kinship());
    }

    @Test
    void testCoverageDetailDtoToBuilder() {
        final CoverageDetailDto coverageDetail = CoverageDetailDto.builder()
                .coverageKey("coverage456")
                .code("COV-002")
                .description("Nueva descripción")
                .mandatory(true)
                .build();
        final CoverageDetailDto modifiedCoverageDetail = CoverageDetailDto.builder()
                .coverageKey("modifiedCoverage")
                .description("Nueva descripción")
                .mandatory(true)
                .build();
        assertEquals("coverage456", coverageDetail.coverageKey());
        assertEquals("COV-002", coverageDetail.code());
        assertTrue(coverageDetail.mandatory());
        assertEquals("modifiedCoverage", modifiedCoverageDetail.coverageKey());
        assertEquals("Nueva descripción", modifiedCoverageDetail.description());
    }


    @Test
    void diffAdminOpDtoTest() {
        final DiffAdminOpDto dto = TestFixtures.diffAdminOpDto();
        assertEquals(30, dto.percentage());
        assertEquals("comparator", dto.comparator());
    }

    @Test
    void yearLimitDtoTest() {
        final YearLimitDto dto = TestFixtures.max();
        assertEquals(69, dto.value());
        assertEquals("YEAR", dto.unit());
    }

    @Test
    void insuredDtoTest() {
        final InsuredDto dto = TestFixtures.insuredDto();
        final DiffAdminOpDto diffAdminOpDto = TestFixtures.diffAdminOpDto();
        assertEquals(7, dto.min());
        assertEquals(1000, dto.max());
        assertEquals(diffAdminOpDto, dto.diffAdminOp());
    }

    @Test
    void catalogDataDtoTest() {
        final CoverageCatalogDto coverageCatalogDto = TestFixtures.createCoverageCatalogDtoToTraditional();
        final CatalogDataDto dto = TestFixtures.createCatalogDataDto();
        assertEquals("Product Description", dto.product());
        assertEquals("Product", dto.description());
        assertEquals(coverageCatalogDto, dto.coverages().getFirst());
    }

    @Test
    void coverageDetailDtoTest() {
        final String coverageKey = "coverage1";
        final String code = "CODE123";
        final String description = "Description";
        final String typeCoverage = "TYPE";
        final Boolean mandatory = true;
        final InsuredValidationDto insuredValidationDto = TestFixtures.insuredValidationDto();
        final List<InsuredValidationDto> insuredValidations = List.of(insuredValidationDto);
        final DependenciesDto dependenciesDto = new DependenciesDto("0003", "T");
        final DefaultValueDto defaultValueDto = new DefaultValueDto(50000, dependenciesDto, null, null);
        final CoverageDetailDto dto = new CoverageDetailDto(coverageKey, code, description, typeCoverage,
                defaultValueDto, mandatory, insuredValidations, null, null, false);
        assertEquals(coverageKey, dto.coverageKey());
        assertEquals(code, dto.code());
        assertEquals(description, dto.description());
        assertEquals(typeCoverage, dto.typeCoverage());
        assertEquals(mandatory, dto.mandatory());
        assertEquals(insuredValidations, dto.insuredValidations());
    }

    @Test
    void testInsuranceDataDtoCreation() {
        final CatalogDataDto catalogDataDto1 = TestFixtures.createCatalogDataDto();
        final CatalogDataDto catalogDataDto2 = TestFixtures.createCatalogDataDto();

        final List<CatalogDataDto> catalogDataDtoList = List.of(catalogDataDto1, catalogDataDto2);

        final InsuranceDataDto insuranceDataDto = new InsuranceDataDto(catalogDataDtoList);

        assertNotNull(insuranceDataDto);
        assertEquals(2, insuranceDataDto.data().size());
        assertEquals("Product Description", insuranceDataDto.data().getFirst().product());
        assertEquals("Product", insuranceDataDto.data().getFirst().description());
    }

    @Test
    void testGetCoverages() {
        final CoverageCatalogDto catalogDataDto = TestFixtures.createCoverageCatalogDtoToTraditional();
        final List<CoverageCatalogDto> catalogDataDtos = List.of(catalogDataDto);
        final DiffAdminOp diffAdminOp = new DiffAdminOp(12, "comparator");
        final Insured insured = new Insured(12, 24, diffAdminOp);
        final Age age = new Age(12, 65, 24, 25, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(50000), new BigDecimal(5000000));
        final YearLimit min = new YearLimit(15, "YEAR");
        final YearLimit max = new YearLimit(69, "YEAR");
        final AgeLimit ageLimitDtoAcceptable = new AgeLimit(min, max);
        final Sum minSum = new Sum(new BigDecimal(5000000), null, "formula", "formula");
        final InsuredSumLimit insuredSumLimit = new InsuredSumLimit(minSum, minSum);
        final InsuredValidation insuredValidation = new InsuredValidation(null, null, null, ageLimitDtoAcceptable,
                null, null, List.of(insuredSumLimit));
        final CoverageDetail coverageDetail = new CoverageDetail("FALLECIMIENTO", "00001", "Fallecimiento",
                "BASICA",  true, null, List.of(insuredValidation),  null, null, null, null);
        final CoverageCatalog catalogModel = new CoverageCatalog("Coverage Code", "Coverage Description", insured,
                age, limitBasic, List.of(coverageDetail), 10, null, null, null, null, null, null, null);
        final List<CoverageCatalog> catalogModels = List.of(catalogModel);
        when(this.catalogMapper.toModelList(catalogDataDtos)).thenReturn(catalogModels);
        final List<CoverageCatalog> result = this.catalogMapper.toModelList(catalogDataDtos);
        assertEquals(1, result.size());
        assertEquals("Coverage Description", result.getFirst().description()); // Asegúrate de usar la descripción correcta
    }

    @Test
    void testCoverageCatalogDtoBuilder() {
        final InsuredDto insured = InsuredDto.builder()
                .min(12)
                .max(30)
                .diffAdminOp(new DiffAdminOpDto(12, "comparator"))
                .build();

        final AgeDto age = AgeDto.builder()
                .min(18)
                .max(65)
                .averageMax(12)
                .averageMin(12)
                .build();
        final List<CoverageDetailDto> coverages = new ArrayList<>();
        final CoverageDetailDto coverageDetail = CoverageDetailDto.builder()
                .coverageKey("coverageKey1")
                .code("COV-001")
                .description("Descripción de la cobertura")
                .typeCoverage("Tipo 1")
                .mandatory(true)
                .insuredValidations(new ArrayList<>())
                .build();

        coverages.add(coverageDetail);

        final CoverageCatalogDto coverageCatalog = CoverageCatalogDto.builder()
                .modalityKey("MOD-001")
                .description("Catalogo de Coberturas")
                .insured(insured)
                .age(age)
                .coverages(coverages)
                .build();

        assertEquals("MOD-001", coverageCatalog.modalityKey());
        assertEquals("Catalogo de Coberturas", coverageCatalog.description());
        assertNotNull(coverageCatalog.insured());
        assertNotNull(coverageCatalog.age());
        assertEquals(1, coverageCatalog.coverages().size());
        assertEquals("coverageKey1", coverageCatalog.coverages().getFirst().coverageKey());
    }

    @Test
    void testCoverageCatalogDtoToBuilder() {
        final InsuredDto insuredDto = TestFixtures.insuredDto();
        final AgeDto ageDto = TestFixtures.ageDto();
        final CoverageDetailDto coverageDetailDto = TestFixtures.coverageDetailDto();
        final CoverageCatalogDto coverageCatalog = CoverageCatalogDto.builder()
                .modalityKey("MOD-002")
                .description("Otro Catalogo de Coberturas")
                .insured(insuredDto)
                .age(ageDto)
                .coverages(List.of(coverageDetailDto))
                .build();

        assertEquals("MOD-002", coverageCatalog.modalityKey());
        assertEquals("Otro Catalogo de Coberturas", coverageCatalog.description());
        assertEquals(coverageDetailDto, coverageCatalog.coverages().getFirst());
    }

    @Test
    void testYearLimitDtoBuilder() {
        final YearLimitDto yearLimitDto = YearLimitDto.builder()
                .value(10)
                .unit("years")
                .build();
        assertEquals(10, yearLimitDto.value());
        assertEquals("years", yearLimitDto.unit());
    }

    @Test
    void testYearLimitDtoToBuilder() {
        final YearLimitDto yearLimitDto = YearLimitDto.builder()
                .value(5)
                .unit("months")
                .build();

        final YearLimitDto modifiedYearLimitDto = YearLimitDto.builder()
                .value(7)
                .unit("days")
                .build();
        assertEquals(5, yearLimitDto.value());
        assertEquals("months", yearLimitDto.unit());
        assertEquals(7, modifiedYearLimitDto.value());
        assertEquals("days", modifiedYearLimitDto.unit());
    }

    @Test
    void testDiffAdminOpDtoBuilder() {
        final DiffAdminOpDto diffAdminOpDto = DiffAdminOpDto.builder()
                .percentage(25)
                .comparator("greater than")
                .build();
        assertEquals(25, diffAdminOpDto.percentage());
        assertEquals("greater than", diffAdminOpDto.comparator());
    }

    @Test
    void testDiffAdminOpDtoToBuilder() {
        final DiffAdminOpDto diffAdminOpDto = DiffAdminOpDto.builder()
                .percentage(10)
                .comparator("equal to")
                .build();

        final DiffAdminOpDto modifiedDiffAdminOpDto = DiffAdminOpDto.builder()
                .percentage(15)
                .comparator("less than")
                .build();

        assertEquals(10, diffAdminOpDto.percentage());
        assertEquals("equal to", diffAdminOpDto.comparator());
        assertEquals(15, modifiedDiffAdminOpDto.percentage());
        assertEquals("less than", modifiedDiffAdminOpDto.comparator());
    }

    @Test
    void testAgeLimitDtoBuilder() {
        final YearLimitDto minYearLimit = YearLimitDto.builder()
                .value(5)
                .unit("years")
                .build();

        final YearLimitDto maxYearLimit = YearLimitDto.builder()
                .value(10)
                .unit("years")
                .build();

        final AgeLimitDto ageLimitDto = AgeLimitDto.builder()
                .min(minYearLimit)
                .max(maxYearLimit)
                .build();
        assertNotNull(ageLimitDto.min());
        assertNotNull(ageLimitDto.max());
        assertEquals(5, ageLimitDto.min().value());
        assertEquals("years", ageLimitDto.min().unit());
        assertEquals(10, ageLimitDto.max().value());
        assertEquals("years", ageLimitDto.max().unit());
    }

    @Test
    void testAgeLimitDtoToBuilder() {
        final YearLimitDto minYearLimit = YearLimitDto.builder()
                .value(3)
                .unit("years")
                .build();

        final YearLimitDto maxYearLimit = YearLimitDto.builder()
                .value(8)
                .unit("years")
                .build();
        final AgeLimitDto ageLimitDto = AgeLimitDto.builder()
                .min(minYearLimit)
                .max(maxYearLimit)
                .build();
        final AgeLimitDto modifiedAgeLimitDto = AgeLimitDto.builder()
                .min(YearLimitDto.builder().value(4).unit("months").build())
                .max(YearLimitDto.builder().value(9).unit("years").build())
                .build();
        assertEquals(3, ageLimitDto.min().value());
        assertEquals(8, ageLimitDto.max().value());

        assertEquals(4, modifiedAgeLimitDto.min().value());
        assertEquals(9, modifiedAgeLimitDto.max().value());
    }

    @Test
    void testDependenciesDtoBuilder() {
        final DependenciesDto dependenciesDto = DependenciesDto.builder()
                .coverageKey("coverage_001")
                .kinshipKey("kinship_001")
                .build();
        assertNotNull(dependenciesDto);
        assertEquals("coverage_001", dependenciesDto.coverageKey());
        assertEquals("kinship_001", dependenciesDto.kinshipKey());
    }

    @Test
    void testDependenciesDtoToBuilder() {
        final DependenciesDto dependenciesDto = DependenciesDto.builder()
                .coverageKey("coverage_002")
                .kinshipKey("kinship_002")
                .build();
        final DependenciesDto modifiedDependenciesDto = DependenciesDto.builder()
                .coverageKey("coverage_updated")
                .kinshipKey("kinship_updated")
                .build();
        assertEquals("coverage_002", dependenciesDto.coverageKey());
        assertEquals("kinship_002", dependenciesDto.kinshipKey());
        assertEquals("coverage_updated", modifiedDependenciesDto.coverageKey());
        assertEquals("kinship_updated", modifiedDependenciesDto.kinshipKey());
    }
}
