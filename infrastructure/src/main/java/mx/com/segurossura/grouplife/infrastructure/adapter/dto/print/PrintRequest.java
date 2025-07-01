package mx.com.segurossura.grouplife.infrastructure.adapter.dto.print;

public record PrintRequest(int oficina, int ramo, String estado,
                           long poliza, int suplemento, String rfc) {
}
