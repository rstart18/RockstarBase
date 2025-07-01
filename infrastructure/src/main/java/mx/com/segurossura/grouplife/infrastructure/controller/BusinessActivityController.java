package mx.com.segurossura.grouplife.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.service.CatalogService;
import mx.com.segurossura.grouplife.openapi.api.BusinessActivitiesApi;
import mx.com.segurossura.grouplife.openapi.model.GetBusinessActivities200ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BusinessActivityController implements BusinessActivitiesApi {
    private static final String LOGGER_PREFIX = String.format("[%s] ", BusinessActivityController.class.getSimpleName());
    private final CatalogService catalogService;
    private final CatalogMapper catalogMapper;

    @Override
    public Mono<ResponseEntity<GetBusinessActivities200ResponseDto>> getBusinessActivities(
            final ServerWebExchange exchange) {
        return this.catalogService.getBusinessActivity()
                .map(this.catalogMapper::toDto)
                .collectList()
                .map(this.catalogMapper::toBusinessActivitiesResponse)
                .map(ResponseEntity::ok);
    }
}
