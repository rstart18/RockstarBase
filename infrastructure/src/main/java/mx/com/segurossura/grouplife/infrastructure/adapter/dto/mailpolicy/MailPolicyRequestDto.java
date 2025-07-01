package mx.com.segurossura.grouplife.infrastructure.adapter.dto.mailpolicy;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MailPolicyRequestDto(
        String oficina,
        String ramo,
        String poliza,
        String suplemento,
        String rfc,
        String estado,
        String email,
        @JsonProperty("emailsCc") List<String> emailccs
) {
}
