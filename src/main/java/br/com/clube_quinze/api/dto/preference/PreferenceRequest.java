package br.com.clube_quinze.api.dto.preference;

import jakarta.validation.constraints.NotBlank;

public record PreferenceRequest(
        @NotBlank(message = "Tipo de preferência é obrigatório")
        String key,
        @NotBlank(message = "Detalhe é obrigatório")
        String value) {
}
