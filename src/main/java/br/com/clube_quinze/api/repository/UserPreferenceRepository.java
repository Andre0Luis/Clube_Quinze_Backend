package br.com.clube_quinze.api.repository;

import br.com.clube_quinze.api.model.user.UserPreference;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    List<UserPreference> findByUserId(Long userId);

    Optional<UserPreference> findByUserIdAndPreferenceKey(Long userId, String preferenceKey);

    Optional<UserPreference> findByIdAndUserId(Long id, Long userId);
}
