package br.com.clube_quinze.api.dto.community;

import jakarta.validation.constraints.NotBlank;

public record PostRequest(
        @NotBlank(message = "Título é obrigatório")
        String title,
        @NotBlank(message = "Conteúdo é obrigatório")
        String content,
        String imageUrl,
        String imageBase64) {
}
