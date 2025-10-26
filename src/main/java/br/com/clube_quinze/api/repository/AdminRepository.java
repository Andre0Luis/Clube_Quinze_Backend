package br.com.clube_quinze.api.repository;

import br.com.clube_quinze.api.model.admin.Admin;
import br.com.clube_quinze.api.model.enumeration.AdminRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);

    List<Admin> findByRole(AdminRole role);
}
