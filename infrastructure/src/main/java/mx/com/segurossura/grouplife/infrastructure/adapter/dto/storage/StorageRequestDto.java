package mx.com.segurossura.grouplife.infrastructure.adapter.dto.storage;

import java.util.List;

public record StorageRequestDto(List<StorageContentRequestDto> files) {
    public record StorageContentRequestDto(String fileName, String content) {
    }
}
