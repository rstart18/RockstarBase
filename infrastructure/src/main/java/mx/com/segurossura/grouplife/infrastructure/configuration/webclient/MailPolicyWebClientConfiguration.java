package mx.com.segurossura.grouplife.infrastructure.configuration.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MailPolicyWebClientConfiguration {

    @Value("${api.mailpolicy.url}")
    private String mailPolicyUrl;

    @Bean
    public WebClient mailPolicyWebClient(final WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(this.mailPolicyUrl)
                .filter(WebClientErrorHandlingUtils.errorHandlingFilter())
                .build();
    }

}
