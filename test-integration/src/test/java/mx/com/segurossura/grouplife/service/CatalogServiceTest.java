package mx.com.segurossura.grouplife.service;

import mx.com.segurossura.grouplife.application.port.CatalogPort;
import mx.com.segurossura.grouplife.application.service.CatalogService;
import mx.com.segurossura.grouplife.domain.model.coverage.Age;
import mx.com.segurossura.grouplife.domain.model.coverage.Comparator;
import mx.com.segurossura.grouplife.domain.model.coverage.CoverageCatalog;
import mx.com.segurossura.grouplife.domain.model.coverage.DiffAdminOp;
import mx.com.segurossura.grouplife.domain.model.coverage.Insured;
import mx.com.segurossura.grouplife.domain.model.coverage.LimitBasic;
import mx.com.segurossura.grouplife.domain.model.coverage.Sami;
import mx.com.segurossura.grouplife.domain.model.enums.Modality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CatalogServiceTest {
    private CatalogService catalogService;
    private CatalogPort catalogPortMock;

    @BeforeEach
    void setUp() {
        this.catalogPortMock = mock(CatalogPort.class);
        this.catalogService = new CatalogService(this.catalogPortMock);
    }

    @Test
    void test_getBusinessActivity() {
        final CatalogItem item1 = new CatalogItem("key1", "value1");
        final CatalogItem item2 = new CatalogItem("key2", "value2");
        when(this.catalogPortMock.getBusinessActivity()).thenReturn(Flux.just(item1, item2));

        StepVerifier.create(this.catalogService.getBusinessActivity())
                .expectNext(item1)
                .expectNext(item2)
                .verifyComplete();

        Mockito.verify(this.catalogPortMock).getBusinessActivity();
    }

    @Test
    void test_getPlansByModality() {
        final Modality modality = Modality.TRADITIONAL;
        final CoveragePlanModality.CoveragePlan item1 = new CoveragePlanModality.CoveragePlan("key1", "value1", List.of(), false, "ujtyu");
        final CoveragePlanModality coveragePlanModality = new CoveragePlanModality(modality, List.of(item1));
        when(this.catalogPortMock.getCoveragePlan()).thenReturn(Flux.just(coveragePlanModality));

        final DiffAdminOp diffAdminOp = new DiffAdminOp(2, "*");
        final Insured insured = new Insured(1, 5, diffAdminOp);
        final Age age = new Age(10, 50, 20, 48, null);
        final LimitBasic limitBasic = new LimitBasic(new BigDecimal(10), new BigDecimal(50));
        final Comparator comparatortStandardDeviation = new Comparator(10, "*");
        final Comparator comparatorActuarialAge = new Comparator(15, "*");
        final Comparator comparatorquotient = new Comparator(20, "*");
        final Sami sami = new Sami(10, 50, 20, 50L);

        final List<CoverageCatalog> coverageCatalogList = List.of(
                new CoverageCatalog("TRADICIONAL", "Modalidad Tradicional", insured, age, limitBasic, List.of(), 10, List.of(sami), null, null,comparatorActuarialAge, comparatortStandardDeviation, comparatorquotient, 40)
        );

        when(this.catalogPortMock.getCoverages()).thenReturn(Mono.just(coverageCatalogList));

        final CoveragePlanWithCoverage coveragePlanWithCoverage = new CoveragePlanWithCoverage("key1", "value1", List.of(), false, "ujtyu");

        StepVerifier.create(this.catalogService.getPlansByModality(modality.getMessageFormat()))
                .expectNext(List.of(coveragePlanWithCoverage))
                .verifyComplete();

        Mockito.verify(this.catalogPortMock).getCoveragePlan();
    }
}
