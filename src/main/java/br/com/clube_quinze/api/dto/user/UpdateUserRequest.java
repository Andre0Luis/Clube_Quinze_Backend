package br.com.clube_quinze.api.dto.user;

import br.com.clube_quinze.api.model.enumeration.MembershipTier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateUserRequest(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        String phone,

        LocalDate birthDate,

        @NotNull(message = "Tipo de membro é obrigatório")
        MembershipTier membershipTier,

        Long planId) {
}
