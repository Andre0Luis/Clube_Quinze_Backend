package br.com.clube_quinze.api.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Token de refresh é obrigatório")
        String refreshToken) {
}
