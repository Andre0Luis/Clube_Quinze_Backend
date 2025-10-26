package br.com.clube_quinze.api.repository;

import br.com.clube_quinze.api.model.community.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    Page<CommunityPost> findByAuthorId(Long authorId, Pageable pageable);
}
