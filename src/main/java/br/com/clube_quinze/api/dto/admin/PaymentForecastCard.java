package br.com.clube_quinze.api.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentForecastCard(Long userId, String userName, BigDecimal amount, LocalDate dueDate) {
}
