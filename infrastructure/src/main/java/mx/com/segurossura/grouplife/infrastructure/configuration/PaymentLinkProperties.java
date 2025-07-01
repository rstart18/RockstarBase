package mx.com.segurossura.grouplife.infrastructure.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "api.payment")
public class PaymentLinkProperties {
    private String direct;
    private String user;
    private String pass;
}
