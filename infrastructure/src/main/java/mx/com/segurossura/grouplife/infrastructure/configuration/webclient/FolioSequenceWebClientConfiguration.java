package mx.com.segurossura.grouplife.infrastructure.configuration.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class FolioSequenceWebClientConfiguration {

    @Value("${api.folio.url}")
    private String folioUrl;

    @Value("${msal.xApiKey}")
    private String xApiKey;

    @Bean
    public WebClient folioSequenceWebClient(final WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(this.folioUrl)
                .filter(new TokenExchangeFilter())
                .defaultHeader("x-api-key", this.xApiKey)
                .filter(WebClientErrorHandlingUtils.errorHandlingFilter())
                .build();
    }

}
