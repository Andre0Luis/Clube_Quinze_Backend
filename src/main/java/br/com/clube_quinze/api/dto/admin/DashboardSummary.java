package br.com.clube_quinze.api.dto.admin;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummary(
        long totalClients,
        long activePlans,
        BigDecimal monthlyRevenue,
        List<UpcomingAppointmentCard> upcomingAppointments,
        List<PaymentForecastCard> upcomingPayments) {
}
