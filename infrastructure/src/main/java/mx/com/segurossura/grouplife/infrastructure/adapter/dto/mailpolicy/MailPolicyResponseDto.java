package mx.com.segurossura.grouplife.infrastructure.adapter.dto.mailpolicy;

public record MailPolicyResponseDto(Data data) {
    public record Data(String message) {
    }
}
