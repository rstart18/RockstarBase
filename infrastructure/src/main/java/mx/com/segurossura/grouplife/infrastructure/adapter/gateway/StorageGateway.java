package mx.com.segurossura.grouplife.infrastructure.adapter.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.StoragePort;
import mx.com.segurossura.grouplife.domain.model.storage.StorageRequest;
import mx.com.segurossura.grouplife.domain.model.storage.StorageResponse;
import mx.com.segurossura.grouplife.infrastructure.adapter.dto.storage.StorageResponseDto;
import mx.com.segurossura.grouplife.infrastructure.exception.customs.GatewayException;
import mx.com.segurossura.grouplife.infrastructure.utils.Utils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageGateway implements StoragePort {

    @Qualifier("storageWebClient")
    private final WebClient storageWebClient;

    private final StorageMapper storageMapper;

    @Override
    public Mono<StorageResponse> getFileUrlStorage(final StorageRequest storageRequest) {
        return this.storageWebClient.post()
                .uri(uriBuilder -> uriBuilder.path("/storage/doc-temp/upload-files").build())
                .bodyValue(this.storageMapper.modelToRequest(storageRequest))
                .retrieve()
                .bodyToMono(StorageResponseDto.class)
                .flatMap(storageResponseDto -> {
                    if (storageResponseDto.data() == null) {
                        log.error("Error when consulting storage. Returning empty Mono.");
                        return Mono.error(new GatewayException("Error", "VG-GTW-NULL-STORAGE"));
                    }
                    final StorageResponse storageResponse = this.storageMapper.responseToModel(storageResponseDto);
                    return Mono.just(storageResponse);
                })
                .doOnError(error -> log.error("Error when consulting storage, body {}  ------> error {}", Utils.toJson(this.storageMapper.modelToRequest(storageRequest)), error.getMessage(), error));
    }

}
