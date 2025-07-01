package mx.com.segurossura.grouplife.infrastructure.configuration.webclient;

import lombok.RequiredArgsConstructor;
import mx.com.segurossura.grouplife.infrastructure.configuration.PaymentLinkProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class PaymentWebClientConfiguration {
    private final PaymentLinkProperties paymentLinkProperties;

    @Bean
    public WebClient paymentUrlWebClient(final WebClient.Builder webClientBuilder) {
        String user = this.paymentLinkProperties.getUser();
        String pass = this.paymentLinkProperties.getPass();
        String basicAuthHeader = "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
        return webClientBuilder
                .baseUrl(this.paymentLinkProperties.getDirect())
                .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .filter(WebClientErrorHandlingUtils.errorHandlingFilter())
                .build();
    }
}
