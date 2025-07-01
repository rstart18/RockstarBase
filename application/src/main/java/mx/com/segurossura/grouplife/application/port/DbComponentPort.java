package mx.com.segurossura.grouplife.application.port;

import reactor.core.publisher.Mono;

public interface DbComponentPort {
    Mono<ComponentPersitenceRecord> createUpdateComponentPersitence(ComponentPersitenceRecord componentPersitenceRecord);
    Mono<ComponentPersitenceRecord> getComponentPersitence(String numberFolio);
    Mono<Integer> getDaysToRecover();
}
