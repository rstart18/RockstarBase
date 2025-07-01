package mx.com.segurossura.grouplife.infrastructure.configuration.webclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.segurossura.grouplife.infrastructure.configuration.IssueProperties;
import mx.com.segurossura.grouplife.infrastructure.configuration.MsalProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

import java.util.Base64;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuotationWebClientConfiguration {
   private final IssueProperties issueProperties;
   private final MsalProperties msalProperties;

    @Bean
    public WebClient quotationWebClient(final WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(this.issueProperties.getUrl())
                .defaultHeader("x-api-key", this.msalProperties.getXApiKey())
                .filter(new TokenExchangeFilter())
                .filter(WebClientErrorHandlingUtils.errorHandlingFilter())
                .build();
    }

    @Bean
    public WebClient issueWebClient(final WebClient.Builder webClientBuilder) {

        String user = this.issueProperties.getUser();
        String pass = this.issueProperties.getPass();
        String basicAuthHeader = "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
        return webClientBuilder
                .baseUrl(this.issueProperties.getDirect())
                .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .filter(WebClientErrorHandlingUtils.errorHandlingFilter())
                .build();
    }
}
