package br.com.clube_quinze.api.dto.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequest(
        @NotNull(message = "Post é obrigatório")
        Long postId,
        @NotNull(message = "Autor é obrigatório")
        Long authorId,
        @NotBlank(message = "Conteúdo é obrigatório")
        String content) {
}
