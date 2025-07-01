package mx.com.segurossura.grouplife.infrastructure.configuration.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PricingWebClientConfiguration {

    @Value("${api.pricing.url}")
    private String pricingUrl;

    @Value("${msal.xApiKey}")
    private String xApiKey;

    @Bean
    public WebClient pricingWebClient(final WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(this.pricingUrl)
                .filter(new TokenExchangeFilter())
                .defaultHeader("x-api-key", this.xApiKey)
                .filter(WebClientErrorHandlingUtils.errorHandlingFilter())
                .build();
    }

}
