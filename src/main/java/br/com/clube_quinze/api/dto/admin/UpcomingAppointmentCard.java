package br.com.clube_quinze.api.dto.admin;

import java.time.LocalDateTime;

public record UpcomingAppointmentCard(Long appointmentId, Long clientId, String clientName, LocalDateTime scheduledAt, String serviceType) {
}
