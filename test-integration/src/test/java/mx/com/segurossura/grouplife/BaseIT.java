package mx.com.segurossura.grouplife;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIT {

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setup() {
        this.reactiveMongoTemplate.dropCollection("cotizaciones_vidaGrupo").block();
        this.reactiveMongoTemplate.dropCollection("cotizaciones_vidaGrupo_componentes").block();
        this.reactiveMongoTemplate.dropCollection("cotizaciones_vidaGrupo_asegurados").block();
        this.reactiveMongoTemplate.createCollection("cotizaciones_vidaGrupo_asegurados").block();
    }
}
