package mx.com.segurossura.grouplife.infrastructure.adapter.dto.coverages;

import lombok.Builder;

import java.util.List;

public record ExecutiveInfoDataResponseDto(List<ExecutiveInfo> data) {
    @Builder
    public record ExecutiveInfo(
            String insuranceAgentKey,
            String nameAgent,
            String pointSaleKey,
            String pointSaleDescription,
            String productKey,
            String subgroupKey,
            String subgroupDescription,
            String profileTariffKey,
            String profileTariffDescription,
            String officeKey,
            String officeDescription,
            String promoterKey,
            String promoterName,
            String banck,
            String executiveKey,
            String executiveName,
            String subaddressKey,
            String subaddressName,
            String managerKey,
            String managerName,
            String regionalKey,
            String desciptionRegional
    ) {
    }
}
