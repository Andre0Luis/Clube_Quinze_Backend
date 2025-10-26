package br.com.clube_quinze.api.dto.payment;

import java.math.BigDecimal;

public record PlanSummary(Long id, String name, String description, BigDecimal price, int durationMonths) {
}
