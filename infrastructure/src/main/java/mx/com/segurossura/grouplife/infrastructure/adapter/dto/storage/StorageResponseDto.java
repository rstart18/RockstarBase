package mx.com.segurossura.grouplife.infrastructure.adapter.dto.storage;

import java.util.List;

public record StorageResponseDto(StorageResponseDataDto data) {
    public record StorageResponseDataDto(String message, List<Files> files){
    }
    public record Files(String name, String url){
    }
}
