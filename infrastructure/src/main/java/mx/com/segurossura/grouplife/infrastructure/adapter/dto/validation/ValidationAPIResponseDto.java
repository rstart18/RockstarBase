package mx.com.segurossura.grouplife.infrastructure.adapter.dto.validation;

public record ValidationAPIResponseDto(boolean success, String message, RenderMessage renderMessage) {
    public record RenderMessage(String title, String body){}
}
