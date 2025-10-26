package br.com.clube_quinze.api.repository;

import br.com.clube_quinze.api.model.community.CommunityComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    List<CommunityComment> findByPostId(Long postId);
}
