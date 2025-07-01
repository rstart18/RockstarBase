package mx.com.segurossura.grouplife.infrastructure.adapter.dto.mailsendgrid;

import java.util.List;
import java.util.Map;

public record MailSendGridRequest(
        String from, List<MailSendGridEMailRequest> to,
        Map<String, Object> dynamicTemplateData, String templateId
        ) {

    public record MailSendGridEMailRequest(String email) {
    }

}
