package br.com.clube_quinze.api.dto.community;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        Long authorId,
        String title,
        String content,
        String imageUrl,
        String imageBase64,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long likeCount,
        List<CommentResponse> comments) {
}
