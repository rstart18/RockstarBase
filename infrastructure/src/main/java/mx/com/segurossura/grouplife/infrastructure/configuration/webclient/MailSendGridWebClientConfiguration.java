package mx.com.segurossura.grouplife.infrastructure.configuration.webclient;

import lombok.RequiredArgsConstructor;
import mx.com.segurossura.grouplife.infrastructure.configuration.MailSendGridProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class MailSendGridWebClientConfiguration {

    private final MailSendGridProperties mailSendGridProperties;

    @Bean
    public WebClient mailSendGridWebClient(final WebClient.Builder webClientBuilder) {

        String user = this.mailSendGridProperties.getUser();
        String pass = this.mailSendGridProperties.getPass();
        String basicAuthHeader = "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());

        return webClientBuilder
                .baseUrl(this.mailSendGridProperties.getDirect())
                .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .defaultHeader("ApiKeySendGrid", this.mailSendGridProperties.getApiKey())
                .filter(WebClientErrorHandlingUtils.errorHandlingFilter())
                .build();
    }
}
