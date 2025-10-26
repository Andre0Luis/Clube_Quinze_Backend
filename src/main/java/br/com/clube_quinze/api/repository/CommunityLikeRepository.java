package br.com.clube_quinze.api.repository;

import br.com.clube_quinze.api.model.community.CommunityLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    Optional<CommunityLike> findByPostIdAndUserId(Long postId, Long userId);

    long countByPostId(Long postId);
}
