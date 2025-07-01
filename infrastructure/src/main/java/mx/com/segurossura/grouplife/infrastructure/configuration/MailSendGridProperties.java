package mx.com.segurossura.grouplife.infrastructure.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "api.mailsendgrid")
public class MailSendGridProperties {
    private String direct;
    private String user;
    private String pass;
    private String from;
    private String apiKey;
    private String templateNotification;
}
