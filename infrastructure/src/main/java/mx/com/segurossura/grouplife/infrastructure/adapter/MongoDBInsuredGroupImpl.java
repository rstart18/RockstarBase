package mx.com.segurossura.grouplife.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mx.com.segurossura.grouplife.application.port.InsuredPort;
import mx.com.segurossura.grouplife.domain.model.insured.AggregateInsuredGroup;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MongoDBInsuredGroupImpl implements InsuredPort {
    private final InsuredGroupRepository insuredGroupRepository;
    private final InsuredGroupMapper insuredGroupMapper;

    @Override
    public Mono<AggregateInsuredGroup> getInsureds(final String numberFolio) {
        return this.insuredGroupRepository.findById_NumberFolio(numberFolio)
                .map(this.insuredGroupMapper::toModelGroupInsureds);
    }
}
