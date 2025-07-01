package mx.com.segurossura.grouplife.controller.testdata;

import mx.com.segurossura.grouplife.openapi.model.AgeLimitDto;
import mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto;
import mx.com.segurossura.grouplife.openapi.model.CoverageDetailInsuredSumCoveragesInnerDto;
import mx.com.segurossura.grouplife.openapi.model.DependenciesDto;
import mx.com.segurossura.grouplife.openapi.model.GroupVgRequestDto;
import mx.com.segurossura.grouplife.openapi.model.GroupVgResponseDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredSumCoveragesCoveragesInformationInnerDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredSumCoveragesDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto;
import mx.com.segurossura.grouplife.openapi.model.SalaryMonthDto;
import mx.com.segurossura.grouplife.openapi.model.SumDto;
import mx.com.segurossura.grouplife.openapi.model.YearLimitDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestFixturesCoverages {

    public static GroupVgRequestDto createGroupVgRequestDtoErrorM_S() {
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("grupo 3 test");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO);
        request.setNumAdministrativeInsured(10);
        request.setNumOperationalInsured(5);
        request.setAdministrativeInsuredSum(new BigDecimal(1440000));
        request.setOperationalInsuredSum(new BigDecimal(1440000));
        request.setSalaryMonth(SalaryMonthDto.NUMBER_36);
        request.setAverageMonthlySalary(120000.0);
        request.setInsuredSumCoverages(createInsuredSumCoveragesInfoDtoMS());
        return request;
    }

    public static GroupVgRequestDto createGroupVgRequestDtoM_SToUpdate() {
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(3);
        request.setName("grupo 3 test");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO);
        request.setNumAdministrativeInsured(10);
        request.setNumOperationalInsured(5);
        request.setAdministrativeInsuredSum(new BigDecimal(1440000));
        request.setOperationalInsuredSum(new BigDecimal(1440000));
        request.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        request.setAverageMonthlySalary(120000.0);
        request.setInsuredSumCoverages(createInsuredSumCoveragesInfoDtoMS());
        return request;
    }

    public static GroupVgRequestDto createGroupVgRequestDtoM_S() {
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("grupo 3 test");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO);
        request.setNumAdministrativeInsured(10);
        request.setNumOperationalInsured(5);
        request.setAdministrativeInsuredSum(new BigDecimal(1440000));
        request.setOperationalInsuredSum(new BigDecimal(1440000));
        request.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        request.setAverageMonthlySalary(120000.0);
        request.setInsuredSumCoverages(createInsuredSumCoveragesInfoDtoMS());
        return request;
    }


    public static GroupVgRequestDto createGroupVgRequestDtoSA_FIJA() {
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("nameGroup");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.SA_FIJA);
        request.setNumAdministrativeInsured(2);
        request.setNumOperationalInsured(1);
        request.setAdministrativeInsuredSum(new BigDecimal(1440000));
        request.setOperationalInsuredSum(new BigDecimal(1000000));
        request.setSalaryMonth(null);
        request.setAverageMonthlySalary(null);
        request.setInsuredSumCoverages(createInsuredSumCoveragesInfoDto());
        return request;
    }

    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesInfoDtoMS() {
        final List<InsuredSumCoveragesDto> result = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto1 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto1.setCode("9842");
        insuredSumCoveragesDto1.setCoveragesInformation(createInsuresInformationDtoMS());
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(createInsuresInformationDtoMS());
        result.add(insuredSumCoveragesDto1);
        result.add(insuredSumCoveragesDto2);
        return result;
    }

    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesInfoDtoError2MS() {
        final List<InsuredSumCoveragesDto> result = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto1 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto1.setCode("9842");
        insuredSumCoveragesDto1.setCoveragesInformation(createInsuresInformationDtoMS());
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(createInsuresInformationDtoMSError());
        result.add(insuredSumCoveragesDto1);
        result.add(insuredSumCoveragesDto2);
        return result;
    }


    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesInfoDtoErrorMS() {
        final List<InsuredSumCoveragesDto> result = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto1 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto1.setCode("90");
        insuredSumCoveragesDto1.setCoveragesInformation(createInsuresInformationDtoMS());
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(createInsuresInformationDtoMS2());
        result.add(insuredSumCoveragesDto1);
        result.add(insuredSumCoveragesDto2);
        return result;
    }
    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesInfoDtoRequest1() {
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSum1 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSum1.setInsuredSum(new BigDecimal(1440000));
        insuredSum1.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.ADMINISTRATIVOS);
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> coveragesInformation = new ArrayList<>();
        coveragesInformation.add(insuredSum1);
        final List<InsuredSumCoveragesDto> result = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto1 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto1.setCode("9842");
        insuredSumCoveragesDto1.setCoveragesInformation(coveragesInformation);
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(coveragesInformation);
        result.add(insuredSumCoveragesDto1);
        result.add(insuredSumCoveragesDto2);
        return result;
    }

    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesInfoDtoRequest2() {
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSum1 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSum1.setInsuredSum(new BigDecimal(1000000));
        insuredSum1.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.OPERATIVOS);
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> coveragesInformation = new ArrayList<>();
        coveragesInformation.add(insuredSum1);
        final List<InsuredSumCoveragesDto> result = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto1 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto1.setCode("9842");
        insuredSumCoveragesDto1.setCoveragesInformation(coveragesInformation);
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(coveragesInformation);
        result.add(insuredSumCoveragesDto1);
        result.add(insuredSumCoveragesDto2);
        return result;
    }

    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesInfoDtoSF() {
        final List<InsuredSumCoveragesDto> result = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto1 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto1.setCode("9842");
        insuredSumCoveragesDto1.setCoveragesInformation(createInsuresInformationDtoSF());
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(createInsuresInformationDtoSF());
        result.add(insuredSumCoveragesDto1);
        result.add(insuredSumCoveragesDto2);
        return result;
    }


    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesInfoDto() {
        final List<InsuredSumCoveragesDto> result = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto1 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto1.setCode("9842");
        insuredSumCoveragesDto1.setCoveragesInformation(createInsuresInformationDto(new BigDecimal(1440000), new BigDecimal(1000000)));
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(createInsuresInformationDto(new BigDecimal(1440000), new BigDecimal(1000000)));
        result.add(insuredSumCoveragesDto1);
        result.add(insuredSumCoveragesDto2);
        return result;
    }

    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesInfoDto23() {
        final List<InsuredSumCoveragesDto> result = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto1 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto1.setCode("9842");
        insuredSumCoveragesDto1.setCoveragesInformation(createInsuresInformationDtoMS23());
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(createInsuresInformationDtoMS23());
        result.add(insuredSumCoveragesDto1);
        result.add(insuredSumCoveragesDto2);
        return result;
    }

    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesInfoDto24() {
        final List<InsuredSumCoveragesDto> result = new ArrayList<>();
        final InsuredSumCoveragesDto insuredSumCoveragesDto1 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto1.setCode("9842");
        insuredSumCoveragesDto1.setCoveragesInformation(createInsuresInformationDtoMS24());
        final InsuredSumCoveragesDto insuredSumCoveragesDto2 = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto2.setCode("9843");
        insuredSumCoveragesDto2.setCoveragesInformation(createInsuresInformationDtoMS24());
        result.add(insuredSumCoveragesDto1);
        result.add(insuredSumCoveragesDto2);
        return result;
    }

    public static List<InsuredSumCoveragesCoveragesInformationInnerDto> createInsuresInformationDtoSF() {
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> result = new ArrayList<>();
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages1 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages1.setInsuredSum(new BigDecimal(1000000));
        insuredSumCoverages1.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.OPERATIVOS);
        result.add(insuredSumCoverages1);
        return result;
    }

    public static List<InsuredSumCoveragesCoveragesInformationInnerDto> createInsuresInformationDtoMS() {
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> result = new ArrayList<>();
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages1 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages1.setInsuredSum(new BigDecimal(1440000));
        insuredSumCoverages1.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.ADMINISTRATIVOS);
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages2 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages2.setInsuredSum(new BigDecimal(1000000));
        insuredSumCoverages2.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.OPERATIVOS);
        result.add(insuredSumCoverages1);
        result.add(insuredSumCoverages2);
        return result;
    }

    public static List<InsuredSumCoveragesCoveragesInformationInnerDto> createInsuresInformationDtoMS24() {
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> result = new ArrayList<>();
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages1 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages1.setInsuredSum(new BigDecimal(1440000));
        insuredSumCoverages1.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.OPERATIVOS);
        result.add(insuredSumCoverages1);
        return result;
    }

    public static List<InsuredSumCoveragesCoveragesInformationInnerDto> createInsuresInformationDtoMS23() {
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> result = new ArrayList<>();
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages1 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages1.setInsuredSum(new BigDecimal(1440000));
        insuredSumCoverages1.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.ADMINISTRATIVOS);
        result.add(insuredSumCoverages1);
        return result;
    }

    public static List<InsuredSumCoveragesCoveragesInformationInnerDto> createInsuresInformationDtoMSError() {
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> result = new ArrayList<>();
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages1 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages1.setInsuredSum(new BigDecimal(4000000));
        insuredSumCoverages1.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.ADMINISTRATIVOS);
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages2 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages2.setInsuredSum(new BigDecimal(5000000));
        insuredSumCoverages2.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.OPERATIVOS);
        result.add(insuredSumCoverages1);
        result.add(insuredSumCoverages2);
        return result;
    }

    public static List<InsuredSumCoveragesCoveragesInformationInnerDto> createInsuresInformationDtoMS2() {
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> result = new ArrayList<>();
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages1 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages1.setInsuredSum(new BigDecimal(1430000));
        insuredSumCoverages1.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.ADMINISTRATIVOS);
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages2 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages2.setInsuredSum(new BigDecimal(1000000));
        insuredSumCoverages2.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.OPERATIVOS);
        result.add(insuredSumCoverages1);
        result.add(insuredSumCoverages2);
        return result;
    }

    public static List<InsuredSumCoveragesCoveragesInformationInnerDto> createInsuresInformationDto(final BigDecimal insuredSum,
                                                                                                    final BigDecimal insuredSumOperatives
    ) {
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> result = new ArrayList<>();
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages1 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();

        insuredSumCoverages1.setInsuredSum(insuredSum);
        insuredSumCoverages1.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.ADMINISTRATIVOS);
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages2 =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages2.setInsuredSum(insuredSumOperatives);
        insuredSumCoverages2.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.OPERATIVOS);
        result.add(insuredSumCoverages1);
        result.add(insuredSumCoverages2);
        return result;
    }

    public static List<CoverageDetailInsuredSumCoveragesInnerDto> createInsuresInformationResultDtoMS2() {
        final List<CoverageDetailInsuredSumCoveragesInnerDto> result = new ArrayList<>();
        final CoverageDetailInsuredSumCoveragesInnerDto insuredSumCoverages1 =
                new CoverageDetailInsuredSumCoveragesInnerDto();
        insuredSumCoverages1.setInsuredSum(new BigDecimal(1000000));
        insuredSumCoverages1.setType("OPERATIVOS");
        result.add(insuredSumCoverages1);
        return result;
    }

    public static List<CoverageDetailInsuredSumCoveragesInnerDto> createInsuresInformationResultDtoMS1() {
        final List<CoverageDetailInsuredSumCoveragesInnerDto> result = new ArrayList<>();
        final CoverageDetailInsuredSumCoveragesInnerDto insuredSumCoverages1 =
                new CoverageDetailInsuredSumCoveragesInnerDto();
        insuredSumCoverages1.setInsuredSum(new BigDecimal(1440000));
        insuredSumCoverages1.setType("ADMINISTRATIVOS");
        result.add(insuredSumCoverages1);
        return result;
    }

    public static List<CoverageDetailInsuredSumCoveragesInnerDto> createInsuresInformationResultDtoMS() {
        final List<CoverageDetailInsuredSumCoveragesInnerDto> result = new ArrayList<>();
        final CoverageDetailInsuredSumCoveragesInnerDto insuredSumCoverages1 =
                new CoverageDetailInsuredSumCoveragesInnerDto();
        insuredSumCoverages1.setInsuredSum(new BigDecimal(1440000));
        insuredSumCoverages1.setType("ADMINISTRATIVOS");
        final CoverageDetailInsuredSumCoveragesInnerDto insuredSumCoverages2 =
                new CoverageDetailInsuredSumCoveragesInnerDto();
        insuredSumCoverages2.setInsuredSum(new BigDecimal(1000000));
        insuredSumCoverages2.setType("OPERATIVOS");
        result.add(insuredSumCoverages1);
        result.add(insuredSumCoverages2);
        return result;
    }

    public static List<CoverageDetailInsuredSumCoveragesInnerDto> createInsuresInformationResultDtoSF(final BigDecimal insuredSumAdm,
                                                                                                      final BigDecimal insuredSumOpera,
                                                                                                      final String administrative,
                                                                                                      final String operative
    ) {
        final List<CoverageDetailInsuredSumCoveragesInnerDto> result = new ArrayList<>();
        final CoverageDetailInsuredSumCoveragesInnerDto insuredSumCoverages1 =
                new CoverageDetailInsuredSumCoveragesInnerDto();
        insuredSumCoverages1.setInsuredSum(insuredSumAdm);
        insuredSumCoverages1.setType(administrative);
        final CoverageDetailInsuredSumCoveragesInnerDto insuredSumCoverages2 =
                new CoverageDetailInsuredSumCoveragesInnerDto();
        insuredSumCoverages2.setInsuredSum(insuredSumOpera);
        insuredSumCoverages2.setType(operative);
        result.add(insuredSumCoverages1);
        result.add(insuredSumCoverages2);
        return result;
    }

    public static GroupVgResponseDto createGroupVgResponseDtoMsToUpdate() {
        final GroupVgResponseDto response = new GroupVgResponseDto();
        response.setGroupNumber(3);
        response.setName("grupo 3 test");
        response.setGroupType("MESES_SUELDO");
        response.setNumAdministrativeInsured(10);
        response.setNumOperationalInsured(5);
        response.setAdministrativeInsuredSum(new BigDecimal(1440000));
        response.setOperationalInsuredSum(new BigDecimal(1440000));
        response.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        response.setAverageMonthlySalary(120000.0);
        response.setCoverages(createCoverageDetailDtoMS());
        return response;
    }

    public static GroupVgResponseDto createGroupVgResponseDtoMs() {
        final GroupVgResponseDto response = new GroupVgResponseDto();
        response.setGroupNumber(1);
        response.setName("grupo 3 test");
        response.setGroupType("MESES_SUELDO");
        response.setNumAdministrativeInsured(10);
        response.setNumOperationalInsured(5);
        response.setAdministrativeInsuredSum(new BigDecimal(1440000));
        response.setOperationalInsuredSum(new BigDecimal(1440000));
        response.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        response.setAverageMonthlySalary(120000.0);
        response.setCoverages(createCoverageDetailDtoMS());
        return response;
    }

    public static List<mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto> createCoverageDetailDto3() {
        final List<mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto> result = new ArrayList<>();
        final mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto coverage1 = new mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto();
        coverage1.coverageKey("FALLECIMIENTO");
        coverage1.code("9842");
        coverage1.description("Fallecimiento");
        coverage1.setTypeCoverage("BASICA");
        coverage1.setMandatory(true);
        coverage1.setInsuredValidations(List.of(createInsureValidationDtoMS()));
        coverage1.setInsuredSumCoverages(createInsuresInformationResultDtoMS());

        final mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto coverage2 = new CoverageDetailDto();
        coverage2.coverageKey("MUERTE_ACCIDENTAL");
        coverage2.code("9843");
        coverage2.description("MUERTE_ACCIDENTAL");
        coverage2.setTypeCoverage("OPCIONAL");
        coverage2.setMandatory(false);
        coverage2.setInsuredValidations(List.of(createInsureValidationDtoMS2()));
        coverage2.setInsuredSumCoverages(createInsuresInformationResultDtoMS());
        result.add(coverage1);
        result.add(coverage2);
        return result;
    }
    public static List<mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto> createCoverageDetailDtoSF2() {
        final List<mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto> result = new ArrayList<>();
        final mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto coverage1 = new mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto();
        coverage1.coverageKey("FALLECIMIENTO");
        coverage1.code("9842");
        coverage1.description("Fallecimiento");
        coverage1.setTypeCoverage("BASICA");
        coverage1.setMandatory(true);
        coverage1.setInsuredValidations(List.of(createInsureValidationDtoMS()));
        coverage1.setInsuredSumCoverages(createInsuresInformationResultDtoMS2());

        final mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto coverage2 = new CoverageDetailDto();
        coverage2.coverageKey("MUERTE_ACCIDENTAL");
        coverage2.code("9843");
        coverage2.description("MUERTE_ACCIDENTAL");
        coverage2.setTypeCoverage("OPCIONAL");
        coverage2.setMandatory(false);
        coverage2.setInsuredValidations(List.of(createInsureValidationDtoMS2()));
        coverage2.setInsuredSumCoverages(createInsuresInformationResultDtoMS2());
        result.add(coverage1);
        result.add(coverage2);
        return result;
    }

    public static List<mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto> createCoverageDetailDtoSF1() {
        final List<mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto> result = new ArrayList<>();
        final mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto coverage1 = new mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto();
        coverage1.coverageKey("FALLECIMIENTO");
        coverage1.code("9842");
        coverage1.description("Fallecimiento");
        coverage1.setTypeCoverage("BASICA");
        coverage1.setMandatory(true);
        coverage1.setInsuredValidations(List.of(createInsureValidationDtoMS()));
        coverage1.setInsuredSumCoverages(createInsuresInformationResultDtoMS1());
        coverage1.setInsuredSumFix(false);

        final mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto coverage2 = new CoverageDetailDto();
        coverage2.coverageKey("MUERTE_ACCIDENTAL");
        coverage2.code("9843");
        coverage2.description("MUERTE_ACCIDENTAL");
        coverage2.setTypeCoverage("OPCIONAL");
        coverage2.setMandatory(false);
        coverage2.setInsuredValidations(List.of(createInsureValidationDtoMS2()));
        coverage2.setInsuredSumCoverages(createInsuresInformationResultDtoMS1());
        coverage2.setInsuredSumFix(false);

        result.add(coverage1);
        result.add(coverage2);
        return result;
    }

    public static List<mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto> createCoverageDetailDtoSF() {
        final List<mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto> result = new ArrayList<>();
        final mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto coverage1 = new mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto();
        coverage1.coverageKey("FALLECIMIENTO");
        coverage1.code("9842");
        coverage1.description("Fallecimiento");
        coverage1.setTypeCoverage("BASICA");
        coverage1.setMandatory(true);
        coverage1.setInsuredValidations(List.of(createInsureValidationDtoMS()));
        coverage1.setInsuredSumCoverages(createInsuresInformationResultDtoMS());
        coverage1.setInsuredSumFix(false);

        final mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto coverage2 = new CoverageDetailDto();
        coverage2.coverageKey("MUERTE_ACCIDENTAL");
        coverage2.code("9843");
        coverage2.description("MUERTE_ACCIDENTAL");
        coverage2.setTypeCoverage("OPCIONAL");
        coverage2.setMandatory(false);
        coverage2.setInsuredValidations(List.of(createInsureValidationDtoMS2()));
        coverage2.setInsuredSumCoverages(createInsuresInformationResultDtoSF(new BigDecimal(1440000), new BigDecimal(1000000), "ADMINISTRATIVOS",
                "OPERATIVOS"));
        coverage2.setInsuredSumFix(false);

        result.add(coverage1);
        result.add(coverage2);
        return result;
    }

    public static List<mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto> createCoverageDetailDtoMS() {
        final List<mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto> result = new ArrayList<>();
        final mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto coverage1 = new mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto();
        coverage1.coverageKey("FALLECIMIENTO");
        coverage1.code("9842");
        coverage1.description("Fallecimiento");
        coverage1.setTypeCoverage("BASICA");
        coverage1.setMandatory(true);
        coverage1.setInsuredValidations(List.of(createInsureValidationDtoMS()));
        coverage1.setInsuredSumCoverages(createInsuresInformationResultDtoMS());
        coverage1.setInsuredSumFix(false);

        final mx.com.segurossura.grouplife.openapi.model.CoverageDetailDto coverage2 = new CoverageDetailDto();
        coverage2.coverageKey("MUERTE_ACCIDENTAL");
        coverage2.code("9843");
        coverage2.description("MUERTE_ACCIDENTAL");
        coverage2.setTypeCoverage("OPCIONAL");
        coverage2.setMandatory(false);
        coverage2.setInsuredValidations(List.of(createInsureValidationDtoMS2()));
        coverage2.setInsuredSumCoverages(createInsuresInformationResultDtoMS());
        coverage2.setInsuredSumFix(false);

        result.add(coverage1);
        result.add(coverage2);
        return result;
    }

    public static mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto createInsureValidationDtoMS() {
        final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDto = new InsuredValidationDto();
        insuredValidationDto.setKinshipKey("T");
        insuredValidationDto.setKinship("Titular");
        insuredValidationDto.setAcceptableYearOldLimit(createAgeLimitDto());
        insuredValidationDto.setInsuredSumLimit(List.of(createInsuredSumLimitDto()));
        return insuredValidationDto;
    }

    public static mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto createInsureValidationDtoMS2() {
        final mx.com.segurossura.grouplife.openapi.model.InsuredValidationDto insuredValidationDto = new InsuredValidationDto();
        insuredValidationDto.setKinshipKey("T");
        insuredValidationDto.setKinship("Titular");
        insuredValidationDto.setAcceptableYearOldLimit(createAgeLimitDto());
        insuredValidationDto.setInsuredSumLimit(List.of(createInsuredSumLimitDtoMS2()));
        return insuredValidationDto;
    }

    public static mx.com.segurossura.grouplife.openapi.model.AgeLimitDto createAgeLimitDto() {
        final mx.com.segurossura.grouplife.openapi.model.AgeLimitDto ageLimitDto = new AgeLimitDto();
        ageLimitDto.setMin(createYearLimitDtoMin());
        ageLimitDto.setMax(createYearLimitDtoMax());
        return ageLimitDto;
    }

    public static mx.com.segurossura.grouplife.openapi.model.YearLimitDto createYearLimitDtoMin() {
        final mx.com.segurossura.grouplife.openapi.model.YearLimitDto min = new mx.com.segurossura.grouplife.openapi.model.YearLimitDto();
        min.setUnit("YEAR");
        min.setValue(69);
        return min;
    }

    public static mx.com.segurossura.grouplife.openapi.model.YearLimitDto createYearLimitDtoMax() {
        final mx.com.segurossura.grouplife.openapi.model.YearLimitDto max = new YearLimitDto();
        max.setUnit("YEAR");
        max.setValue(60);
        return max;
    }

    public static mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto createInsuredSumLimitDto() {
        final mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto insuredSumLimitDto = new InsuredSumLimitDto();
        insuredSumLimitDto.setMin(createSumDtoMinMs());
        insuredSumLimitDto.setMax(createSumDtoMaxMS());
        return insuredSumLimitDto;
    }

    public static mx.com.segurossura.grouplife.openapi.model.SumDto createSumDtoMinMs() {
        final mx.com.segurossura.grouplife.openapi.model.SumDto min = new mx.com.segurossura.grouplife.openapi.model.SumDto();
        min.setDefaultValue(new BigDecimal(50000));
        min.dependencies(null);
        min.setFormulaDescription(null);
        min.setFormula(null);
        return min;
    }

    public static mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto createInsuredSumLimitDtoMS2() {
        final mx.com.segurossura.grouplife.openapi.model.InsuredSumLimitDto insuredSumLimitDto = new InsuredSumLimitDto();
        insuredSumLimitDto.setMin(createSumDtoMinMs2(new BigDecimal(50000)));
        insuredSumLimitDto.setMax(createSumDtoMaxMS2());
        return insuredSumLimitDto;
    }

    public static mx.com.segurossura.grouplife.openapi.model.SumDto createSumDtoMaxMS2() {
        final mx.com.segurossura.grouplife.openapi.model.SumDto max = new SumDto();
        max.setDefaultValue(new BigDecimal(5000000));
        max.dependencies(createDependenciesDto2Ms());
        max.setFormulaDescription("La suma asegurada de la cobertura muerte accidental Colectiva debe ser menor o igual a la suma asegurada de la cobertura 9842.");
        max.setFormula("({InsuredSum} <= dependencies)");
        return max;
    }

    public static mx.com.segurossura.grouplife.openapi.model.SumDto createSumDtoMinMs2(final BigDecimal defaultValue) {
        final mx.com.segurossura.grouplife.openapi.model.SumDto min = new mx.com.segurossura.grouplife.openapi.model.SumDto();
        min.setDefaultValue(defaultValue);
        min.dependencies(null);
        min.setFormulaDescription(null);
        min.setFormula(null);
        return min;
    }

    public static mx.com.segurossura.grouplife.openapi.model.SumDto createSumDtoMaxMS() {
        final mx.com.segurossura.grouplife.openapi.model.SumDto max = new SumDto();
        max.setDefaultValue(new BigDecimal(5000000));
        max.dependencies(null);
        max.setFormulaDescription(null);
        max.setFormula(null);
        return max;
    }

    public static mx.com.segurossura.grouplife.openapi.model.DependenciesDto createDependenciesDto2Ms() {
        final mx.com.segurossura.grouplife.openapi.model.DependenciesDto dependenciesDto = new DependenciesDto();
        dependenciesDto.kinshipKey("T");
        dependenciesDto.setCoverageKey("9842");
        return dependenciesDto;
    }

}
