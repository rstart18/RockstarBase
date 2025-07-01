package mx.com.segurossura.grouplife.infrastructure.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mail-notification")
public class MailNotificationProperties {

    private String subject;
    private String subjectError;
    private Success success;
    private Error error;

    @Getter
    @Setter
    public static class Success {
        private String img;
        private String title;
        private String titleColor;
        private String bodyCardTraditional;
        private String bodyLinkTraditional;
        private String bodyTransferTraditional;
        private String bodyCard;
        private String bodyLink;
        private String bodyTransfer;
    }

    @Getter
    @Setter
    public static class Error {
        private String img;
        private String title;
        private String titleColor;
        private String body;
        private String bodyTraditional;
    }
}
