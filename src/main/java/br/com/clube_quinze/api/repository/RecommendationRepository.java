package br.com.clube_quinze.api.repository;

import br.com.clube_quinze.api.model.community.Recommendation;
import br.com.clube_quinze.api.model.enumeration.SuggestionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByStatus(SuggestionStatus status);
}
