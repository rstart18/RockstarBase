package mx.com.segurossura.grouplife.infrastructure.adapter;

import io.github.resilience4j.reactor.retry.RetryOperator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.application.port.DbComponentPort;
import mx.com.segurossura.grouplife.infrastructure.configuration.RecoverProperties;
import mx.com.segurossura.grouplife.infrastructure.repository.entity.components.ComponentRecordEntity;
import mx.com.segurossura.grouplife.infrastructure.repository.persistencerepository.ComponentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import io.github.resilience4j.retry.Retry;

@Service
@Slf4j
@AllArgsConstructor
public class  MongoDBComponentServiceImpl implements DbComponentPort {

    private final ComponentRepository componentRepository;
    private final ComponentMapper componentMapper;
    private final RecoverProperties recoverProperties;

    @Qualifier("mongoDbRetry")
    private final Retry mongoDbRetry;

    @Override
    public Mono<ComponentPersitenceRecord> createUpdateComponentPersitence(ComponentPersitenceRecord componentPersitenceRecord) {

        ComponentRecordEntity entity = this.componentMapper.toEntity(componentPersitenceRecord);
        return this.componentRepository.findById_NumberFolio(componentPersitenceRecord.numberFolio())
                .transformDeferred(RetryOperator.of(this.mongoDbRetry))
                .flatMap(existingComponent -> {
                    existingComponent.setState(entity.getState());
                    existingComponent.setComponents(entity.getComponents());
                    existingComponent.setCurrentStep(entity.getCurrentStep());
                    return this.componentRepository.save(existingComponent);
                })
                .switchIfEmpty(Mono.defer(() -> this.componentRepository.save(entity).transformDeferred(RetryOperator.of(this.mongoDbRetry))))
                .map(this.componentMapper::toModel);
    }

    @Override
    public Mono<ComponentPersitenceRecord> getComponentPersitence(String numberFolio) {
        return this.componentRepository.findById_NumberFolio(numberFolio)
                .transformDeferred(RetryOperator.of(this.mongoDbRetry))
                .switchIfEmpty(Mono.error(new FolioRecordException.FolioRecordNotFound("VG-MDB-001")))
                .map(this.componentMapper::toModel);
    }

    @Override
    public Mono<Integer> getDaysToRecover() {
        return Mono.just(recoverProperties.getDaysToRecover());
    }
}
