package mx.com.segurossura.grouplife.infrastructure.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PropertiesConfiguration {

    @Value("${api.pricing.version}")
    private String pricingVersion;

}
