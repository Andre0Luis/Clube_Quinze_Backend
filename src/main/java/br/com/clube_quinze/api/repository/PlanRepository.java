package br.com.clube_quinze.api.repository;

import br.com.clube_quinze.api.model.payment.Plan;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findByName(String name);
}
