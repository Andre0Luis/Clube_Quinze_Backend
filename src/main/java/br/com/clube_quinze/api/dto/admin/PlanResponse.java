package br.com.clube_quinze.api.dto.admin;

import java.math.BigDecimal;

public record PlanResponse(Long id, String name, String description, BigDecimal price, int durationMonths) {
}
