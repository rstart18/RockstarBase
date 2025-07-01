package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.PricingPort;
import mx.com.segurossura.grouplife.domain.model.pricing.PlanDataResponse;
import mx.com.segurossura.grouplife.domain.model.pricing.Pricing;
import mx.com.segurossura.grouplife.domain.model.pricing.PricingVolunteerResponse;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.PlanDataResponseDto;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.pricing.response.PricingVolunteerResponseDto;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingGateway implements PricingPort {

    @Qualifier("pricingWebClient")
    private final WebClient pricingWebClient;

    private final PricingMapper pricingMapper;

    @Override
    public Mono<PlanDataResponse.PlanResponseList> getPricing(final Pricing pricing) {

        return this.pricingWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/life-product/get").build())
                .bodyValue(this.pricingMapper.modelToRequest(pricing))
                .retrieve()
                .bodyToMono(PlanDataResponseDto.class)
                .flatMap(planDataResponseDto -> {
                    if (planDataResponseDto.data() == null || planDataResponseDto.data().plans().isEmpty()) {
                        log.error("Error when consulting pricing. Returning empty Mono.");
                        return Mono.error(new GatewayException("Error", "VG-GTW-NULL-PRICING"));
                    }
                    final List<PlanDataResponse.PlanResponse> planResponses = this.pricingMapper.responseToModelList(planDataResponseDto);
                    return Mono.just(new PlanDataResponse.PlanResponseList(planResponses, null));
                })
                .doOnError(error -> log.error("Error when consulting pricing, body {} error {}", this.pricingMapper.modelToRequest(pricing), error.getMessage(), error));
    }

    @Override
    public Mono<PlanDataResponse.PlanResponseList> getPricingVolunteer(final Pricing pricing) {

        return this.pricingWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/life-product-volunteering/get").build())
                .bodyValue(this.pricingMapper.modelToRequest(pricing))
                .retrieve()
                .bodyToMono(PricingVolunteerResponseDto.class)
                .flatMap(pricingVolunteerResponseDto -> {
                    if (pricingVolunteerResponseDto.data() == null) {
                        log.error("Error when consulting pricing. Returning empty Mono.");
                        return Mono.error(new GatewayException("Error", "VG-GTW-NULL-PRICING"));
                    }
                    final PricingVolunteerResponse pricingVolunteerResponse = this.pricingMapper.dtoToPricingVolunteerResponse(pricingVolunteerResponseDto);
                    return Mono.just(new PlanDataResponse.PlanResponseList(null, pricingVolunteerResponse));
                })
                .doOnError(error -> log.error("Error when consulting pricing, body {} error {}", this.pricingMapper.modelToRequest(pricing), error.getMessage(), error));
    }
}
