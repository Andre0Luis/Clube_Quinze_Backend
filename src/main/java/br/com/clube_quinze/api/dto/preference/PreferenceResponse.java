package br.com.clube_quinze.api.dto.preference;

import java.time.LocalDateTime;

public record PreferenceResponse(Long id, String key, String value, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
