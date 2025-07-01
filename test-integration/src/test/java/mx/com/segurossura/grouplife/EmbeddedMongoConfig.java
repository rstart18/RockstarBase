package mx.com.segurossura.grouplife;

import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;
import org.springframework.context.annotation.Configuration;

@Configuration
class EmbeddedMongoConfig {
    private static TransitionWalker.ReachedState<RunningMongodProcess> running;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (running != null && running.current() != null) {
                running.current().stop();
            }
        }));

        final Mongod serverMongoDB = Mongod.builder()
                .net(Start.to(Net.class).initializedWith(Net.defaults().withPort(28017))) // Cambia el puerto aquí
                .build();
        running = serverMongoDB.start(Version.Main.V8_0_RC);
    }

    private TransitionWalker.ReachedState<RunningMongodProcess> runningMongoInstance;

//    @Bean
//    public TransitionWalker.ReachedState<RunningMongodProcess> embeddedMongoServer() {
//        final Mongod serverMongoDB = Mongod.builder()
//                .net(Start.to(Net.class).initializedWith(Net.defaults().withPort(28017)))
//                .build();
//        this.runningMongoInstance = serverMongoDB.start(Version.Main.V8_0_RC);
//        return this.runningMongoInstance;
//    }
//
//    @PreDestroy
//    public void stopEmbeddedMongo() {
//        if (this.runningMongoInstance != null && this.runningMongoInstance.current() != null) {
//            this.runningMongoInstance.current().stop();
//        }
//    }
}
