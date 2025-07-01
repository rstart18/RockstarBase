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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestCoverages {

    public static InsuranceDataDto createInsuranceDataDto() {
        final List<CatalogDataDto> catalogDataList = new ArrayList<>();
        catalogDataList.add(createCatalogDataDto());

        return new InsuranceDataDto(catalogDataList);
    }

    public static CatalogDataDto createCatalogDataDto() {
        final List<CoverageCatalogDto> coverageCatalogList = new ArrayList<>();
        final CoverageCatalogDto coverage1 = createCoverageSCatalog1DtoToTraditional();
        coverageCatalogList.add(coverage1);
        return new CatalogDataDto("Product Description", "Product", coverageCatalogList);
    }

    public static CoverageCatalogDto createCoverageSCatalog1DtoToTraditional() {
        final CoverageDetailDto coverages1 = coverageDetail1Dto();
        final CoverageDetailDto coverages2 = coverageDetail2Dto();
        final List<CoverageDetailDto> coverages = new ArrayList<>();
        coverages.add(coverages1);
        coverages.add(coverages2);
        return new CoverageCatalogDto("TRADICIONAL", "Coverage Description", insuredDto(), ageDto(), coverages, 10,
                new LimitBasic(43, 25)
                ,null, null,null, new ComparatorDto(25, "comparator"), null, null, null);
    }

    public static CoverageDetailDto coverageDetail2Dto() {
        return new CoverageDetailDto("MUERTE_ACCIDENTAL", "9843", "MUERTE_ACCIDENTAL",
                "OPCIONAL", null, false, List.of(createInsureValidation2Dto()), null, null, false);

    }

    public static InsuredValidationDto createInsureValidation2Dto() {
        return new InsuredValidationDto(null, "T", "Titular", createAgeLimitDto(),
                createAgeLimitDto(), createAgeLimitDto(), insuredSumLimit2Dtos());
    }

    public static List<InsuredSumLimitDto> insuredSumLimit2Dtos() {
        final InsuredSumLimitDto insuredSumLimit1 = new InsuredSumLimitDto(create1SumMin(), create2SumMax());
        final List<InsuredSumLimitDto> result = new ArrayList<>();
        result.add(insuredSumLimit1);
        return result;
    }

    public static SumDto create2SumMax() {
        return new SumDto(new BigDecimal(5000000), create2Dependencies(), "La suma asegurada de la cobertura" +
                " muerte accidental Colectiva debe ser menor o igual a la suma asegurada de la cobertura 9842.",
                "({InsuredSum} <= dependencies)");
    }

    public static DependenciesDto create2Dependencies() {
        return new DependenciesDto("9842", "T");
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
}
