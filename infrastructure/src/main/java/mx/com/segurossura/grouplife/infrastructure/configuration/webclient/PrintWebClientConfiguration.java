package mx.com.segurossura.grouplife.infrastructure.configuration.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PrintWebClientConfiguration {

    @Value("${api.print.url}")
    private String printUrl;

    @Bean
    public WebClient printWebClient(final WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(this.printUrl)
                .filter(WebClientErrorHandlingUtils.errorHandlingFilter())
                .build();
    }

}
