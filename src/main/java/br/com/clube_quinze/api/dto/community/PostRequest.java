package br.com.clube_quinze.api.dto.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostRequest(
        @NotNull(message = "Autor é obrigatório")
        Long authorId,
        @NotBlank(message = "Título é obrigatório")
        String title,
        @NotBlank(message = "Conteúdo é obrigatório")
        String content,
        String imageUrl,
        String imageBase64) {
}
