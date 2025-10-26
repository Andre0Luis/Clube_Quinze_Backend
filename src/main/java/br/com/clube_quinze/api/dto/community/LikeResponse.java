package br.com.clube_quinze.api.dto.community;

import java.time.LocalDateTime;

public record LikeResponse(Long id, Long postId, Long userId, LocalDateTime createdAt) {
}
