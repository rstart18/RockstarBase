package mx.com.segurossura.grouplife.infrastructure.adapter.dto.mailsendgrid;

public record MailSendGridResponse(
        boolean success, String messages, Object mail, String messageId
) {
}
