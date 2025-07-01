package mx.com.segurossura.grouplife.application.service;

import lombok.RequiredArgsConstructor;
import mx.com.segurossura.grouplife.application.port.CatalogPort;
import mx.com.segurossura.grouplife.application.port.DbPort;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageCatalog;
import mx.com.segurossura.grouplife.domain.model.enums.Modality;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ServiceUtils {

    private final DbPort dbPort;

    private final CatalogPort catalogPort;

    public Mono<FolioRecord> getFolioRecord(final String numberFolio) {
        return this.dbPort.findFolioRecord(numberFolio);
    }

    public Mono<CoverageCatalog> getCoverageCatalog(final Modality modalityKey) {
        return this.catalogPort.getCoverages()
                .flatMap(coverageCatalogList -> Mono.justOrEmpty(coverageCatalogList.stream()
                        .filter(coverageCatalog -> coverageCatalog.modalityKey().equals(modalityKey.getMessageFormat()))
                        .findFirst()));
    }
}
