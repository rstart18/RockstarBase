package mx.com.segurossura.grouplife.infrastructure.repository.persistencerepository;

import mx.com.segurossura.grouplife.infrastructure.repository.entity.FolioNumber;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.components.ComponentRecordEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ComponentRepository extends ReactiveMongoRepository<ComponentRecordEntity, FolioNumber> {
    Mono<ComponentRecordEntity> findById_NumberFolio(String numberFolio);
}
