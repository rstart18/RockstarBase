package mx.com.segurossura.grouplife.controller.testdata;

import mx.com.segurossura.grouplife.domain.model.coverage.Sami;
import mx.com.segurossura.grouplife.openapi.model.GroupVgRequestDto;
import mx.com.segurossura.grouplife.openapi.model.GroupVgResponseDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredSumCoveragesCoveragesInformationInnerDto;
import mx.com.segurossura.grouplife.openapi.model.InsuredSumCoveragesDto;
import mx.com.segurossura.grouplife.openapi.model.SalaryMonthDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestFixturesGroup {
    public static GroupVgRequestDto createGroupVgRequestDtoSA_FIJA() {
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("nameGroup");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.SA_FIJA);
        request.setNumAdministrativeInsured(12);
        request.setNumOperationalInsured(12);
        request.setAdministrativeInsuredSum(BigDecimal.valueOf(13));
        request.setOperationalInsuredSum(BigDecimal.valueOf(13));
        request.setSalaryMonth(null);
        request.setAverageMonthlySalary(null);
        request.setInsuredSumCoverages(List.of(createInsuredSumCoveragesDto()));
        return request;
    }

    public static GroupVgRequestDto createGroupVgRequestDtoSA_FIJADataInvalid(final String name,
                                                                              final GroupVgRequestDto.GroupTypeEnum type,
                                                                              final Integer numAdministrativeInsured,
                                                                              final Integer numOperationalInsured,
                                                                              final BigDecimal administrativeInsuredSum,
                                                                              final BigDecimal operationalInsuredSum,
                                                                              final List<InsuredSumCoveragesDto> coverages) {
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName(name);
        request.setGroupType(type);
        request.setNumAdministrativeInsured(numAdministrativeInsured);
        request.setNumOperationalInsured(numOperationalInsured);
        request.setAdministrativeInsuredSum(administrativeInsuredSum);
        request.setOperationalInsuredSum(operationalInsuredSum);
        request.setSalaryMonth(null);
        request.setAverageMonthlySalary(null);
        request.setInsuredSumCoverages(coverages);
        return request;
    }

    public static GroupVgRequestDto createGroupVgRequestDto() {
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(null);
        request.setName("nameGroup");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.SA_FIJA);
        request.setNumAdministrativeInsured(12);
        request.setNumOperationalInsured(12);
        request.setAdministrativeInsuredSum(BigDecimal.valueOf(13));
        request.setOperationalInsuredSum(BigDecimal.valueOf(13));
        request.setSalaryMonth(null);
        request.setAverageMonthlySalary(null);
        request.setInsuredSumCoverages(List.of(createInsuredSumCoveragesDto()));
        return request;
    }

    public static GroupVgRequestDto createGroupVgRequestDtoAndGroupNumberDoesNotExist() {
        final GroupVgRequestDto request = new GroupVgRequestDto();
        request.setGroupNumber(9);
        request.setName("nameGroup");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.MESES_SUELDO);
        request.setNumAdministrativeInsured(2);
        request.setNumOperationalInsured(1);
        request.setAdministrativeInsuredSum(BigDecimal.valueOf(1440000));
        request.setOperationalInsuredSum(BigDecimal.valueOf(1440000));
        request.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        request.setAverageMonthlySalary(120000.0);
        request.setGroupNumber(6);
        request.setName("nameGroup");
        request.setGroupType(GroupVgRequestDto.GroupTypeEnum.SA_FIJA);
        request.setNumAdministrativeInsured(12);
        request.setNumOperationalInsured(12);
        request.setAdministrativeInsuredSum(BigDecimal.valueOf(13));
        request.setOperationalInsuredSum(BigDecimal.valueOf(13));
        request.setSalaryMonth(null);
        request.setAverageMonthlySalary(null);
        request.setInsuredSumCoverages(List.of(createInsuredSumCoveragesDto()));
        return request;
    }

    public static InsuredSumCoveragesDto createInsuredSumCoveragesDto() {
        final InsuredSumCoveragesDto insuredSumCoveragesDto = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto.setCode("00001");
        insuredSumCoveragesDto.setCoveragesInformation(List.of(createInsureInformationDto()));
        return insuredSumCoveragesDto;
    }

    public static List<InsuredSumCoveragesDto> createInsuredSumCoveragesInvalidDto() {
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesOperat =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesOperat.setInsuredSum(BigDecimal.valueOf(10000));
        insuredSumCoveragesOperat.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.OPERATIVOS);
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoveragesAdmin =
                new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoveragesAdmin.setInsuredSum(BigDecimal.valueOf(10000));
        insuredSumCoveragesAdmin.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.ADMINISTRATIVOS);
        final List<InsuredSumCoveragesCoveragesInformationInnerDto> insuredSums = new ArrayList<>();
        insuredSums.add(insuredSumCoveragesOperat);
        insuredSums.add(insuredSumCoveragesAdmin);
        final InsuredSumCoveragesDto insuredSumCoveragesDto = new InsuredSumCoveragesDto();
        insuredSumCoveragesDto.setCode("12223333");
        insuredSumCoveragesDto.setCoveragesInformation(insuredSums);
        return new ArrayList<>();
    }

    public static InsuredSumCoveragesCoveragesInformationInnerDto createInsureInformationDtoError2() {
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages = new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages.setInsuredSum(BigDecimal.valueOf(70000000));
        insuredSumCoverages.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.ADMINISTRATIVOS);
        return insuredSumCoverages;
    }

    public static InsuredSumCoveragesCoveragesInformationInnerDto createInsureInformationDtoError() {
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages = new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages.setInsuredSum(BigDecimal.valueOf(9000000));
        insuredSumCoverages.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.ADMINISTRATIVOS);
        return insuredSumCoverages;
    }

    public static InsuredSumCoveragesCoveragesInformationInnerDto createInsureInformationDto() {
        final InsuredSumCoveragesCoveragesInformationInnerDto insuredSumCoverages = new InsuredSumCoveragesCoveragesInformationInnerDto();
        insuredSumCoverages.setInsuredSum(BigDecimal.valueOf(10000));
        insuredSumCoverages.setType(InsuredSumCoveragesCoveragesInformationInnerDto.TypeEnum.ADMINISTRATIVOS);
        return insuredSumCoverages;
    }

    public static GroupVgResponseDto createGroupVgResponseDtoSA_FIJAData1() {
        final GroupVgResponseDto response = new GroupVgResponseDto();
        response.setGroupNumber(1);
        response.setName("nameGroup");
        response.setGroupType("SA_FIJA");
        response.setNumAdministrativeInsured(1);
        response.setNumOperationalInsured(0);
        response.setAdministrativeInsuredSum(BigDecimal.valueOf(1440000));
        response.setOperationalInsuredSum(null);
        response.setSalaryMonth(null);
        response.setAverageMonthlySalary(null);
        response.setCoverages(TestFixturesCoverages.createCoverageDetailDtoSF1());
        return response;
    }

    public static GroupVgResponseDto createGroupVgResponseDtoSA_FIJAData2() {
        final GroupVgResponseDto response = new GroupVgResponseDto();
        response.setGroupNumber(1);
        response.setName("nameGroup");
        response.setGroupType("SA_FIJA");
        response.setNumAdministrativeInsured(0);
        response.setNumOperationalInsured(1);
        response.setAdministrativeInsuredSum(null);
        response.setOperationalInsuredSum(BigDecimal.valueOf(1000000));
        response.setSalaryMonth(null);
        response.setAverageMonthlySalary(null);
        response.setCoverages(TestFixturesCoverages.createCoverageDetailDtoSF2());
        return response;
    }

    public static GroupVgResponseDto createGroupVgResponseDtoMESES_SUELDO() {
        final GroupVgResponseDto response = new GroupVgResponseDto();
        response.setGroupNumber(1);
        response.setName("grupo Meses Sueldo");
        response.setGroupType("MESES_SUELDO");
        response.setNumAdministrativeInsured(2);
        response.setNumOperationalInsured(1);
        response.setAdministrativeInsuredSum(BigDecimal.valueOf(1440000));
        response.setOperationalInsuredSum(BigDecimal.valueOf(1440000));
        response.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        response.setAverageMonthlySalary(120000.0);
        response.setCoverages(TestFixturesCoverages.createCoverageDetailDto3());
        return response;
    }

    public static GroupVgResponseDto createGroupVgResponseDtoMESES_SUELDO2() {
        final GroupVgResponseDto response = new GroupVgResponseDto();
        response.setGroupNumber(1);
        response.setName("grupo Meses Sueldo");
        response.setGroupType("MESES_SUELDO");
        response.setNumAdministrativeInsured(1);
        response.setNumOperationalInsured(0);
        response.setAdministrativeInsuredSum(BigDecimal.valueOf(1440000));
        response.setOperationalInsuredSum(BigDecimal.valueOf(1440000));
        response.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        response.setAverageMonthlySalary(120000.0);
        response.setCoverages(TestFixturesCoverages.createCoverageDetailDtoSF());
        return response;
    }

    public static GroupVgResponseDto createGroupVgResponseDtoMESES_SUELDO1() {
        final GroupVgResponseDto response = new GroupVgResponseDto();
        response.setGroupNumber(1);
        response.setName("grupo Meses Sueldo");
        response.setGroupType("MESES_SUELDO");
        response.setNumAdministrativeInsured(0);
        response.setNumOperationalInsured(1);
        response.setAdministrativeInsuredSum(BigDecimal.valueOf(1440000));
        response.setOperationalInsuredSum(BigDecimal.valueOf(1440000));
        response.setSalaryMonth(SalaryMonthDto.NUMBER_12);
        response.setAverageMonthlySalary(120000.0);
        response.setCoverages(TestFixturesCoverages.createCoverageDetailDtoSF());
        return response;
    }

    public static GroupVgResponseDto createGroupVgResponseDtoSA_FIJA() {
        final GroupVgResponseDto response = new GroupVgResponseDto();
        response.setGroupNumber(1);
        response.setName("nameGroup");
        response.setGroupType("SA_FIJA");
        response.setNumAdministrativeInsured(2);
        response.setNumOperationalInsured(1);
        response.setAdministrativeInsuredSum(BigDecimal.valueOf(1440000));
        response.setOperationalInsuredSum(BigDecimal.valueOf(1000000));
        response.setSalaryMonth(null);
        response.setAverageMonthlySalary(null);
        response.setCoverages(TestFixturesCoverages.createCoverageDetailDtoSF());
        return response;
    }

    public static List<Sami> createListSami() {
        final List<Sami> result = new ArrayList<>();
        final Sami sami1 = new Sami(7, 24, 2, 2500000L);
        final Sami sami2 = new Sami(25, 49, 3, 2550000L);
        final Sami sami3 = new Sami(50, 99, 4, 3100000L);
        final Sami sami4 = new Sami(100, 149, 5, 3600000L);
        final Sami sami5 = new Sami(150, 199, 6, 4200000L);
        final Sami sami6 = new Sami(200, 299, 7, 4500000L);
        final Sami sami7 = new Sami(300, 399, 8, 4800000L);
        final Sami sami8 = new Sami(400, 499, 9, 5000000L);
        final Sami sami9 = new Sami(500, 649, 10, 5000000L);
        final Sami sami10 = new Sami(650, 649, 11, 5000000L);
        final Sami sami11 = new Sami(800, 999, 12, 5000000L);
        final Sami sami12 = new Sami(1000, 1000, 13, 5000000L);
        result.add(sami1);
        result.add(sami2);
        result.add(sami3);
        result.add(sami4);
        result.add(sami5);
        result.add(sami6);
        result.add(sami7);
        result.add(sami8);
        result.add(sami9);
        result.add(sami10);
        result.add(sami11);
        result.add(sami12);
        return result;
    }
}
