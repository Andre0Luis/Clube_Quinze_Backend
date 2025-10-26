package br.com.clube_quinze.api.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentForecastResponse(Long userId, String userName, BigDecimal amount, LocalDate dueDate) {
}
