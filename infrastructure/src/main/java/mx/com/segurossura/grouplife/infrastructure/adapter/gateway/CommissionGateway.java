package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.CommissionPort;
import mx.com.segurossura.grouplife.domain.model.comission.CommissionAgent;
import mx.com.segurossura.grouplife.domain.model.comission.CommissionRequest;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.comission.CommissionDataResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommissionGateway implements CommissionPort {

    private final QuotationDetailsMapper quotationDetailsMapper;
    @Qualifier("commissionWebClient")
    private final WebClient commissionWebClient;

    @Override
    public Mono<CommissionAgent> getComissionsAgent(CommissionRequest request) {

        return this.commissionWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/commissions-agents/get").build())
                .bodyValue(this.quotationDetailsMapper.domainToRequestDto(request))
                .retrieve()
                .bodyToMono(CommissionDataResponseDto.class)
                .map(commissionDataResponseDto -> this.quotationDetailsMapper.responseDtoToDomain(commissionDataResponseDto.data()));
    }
}
