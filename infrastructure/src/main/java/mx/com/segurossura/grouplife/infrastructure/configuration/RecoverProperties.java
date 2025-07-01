package mx.com.segurossura.grouplife.infrastructure.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class RecoverProperties {
    @Value("${api.folio.daystorecover}")
    private Integer daysToRecover;
}