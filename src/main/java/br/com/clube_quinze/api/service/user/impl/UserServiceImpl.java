package br.com.clube_quinze.api.service.user.impl;

import br.com.clube_quinze.api.dto.appointment.AppointmentResponse;
import br.com.clube_quinze.api.dto.payment.PlanSummary;
import br.com.clube_quinze.api.dto.preference.PreferenceResponse;
import br.com.clube_quinze.api.dto.user.UpdateUserRequest;
import br.com.clube_quinze.api.dto.user.UserProfileResponse;
import br.com.clube_quinze.api.exception.BusinessException;
import br.com.clube_quinze.api.exception.ResourceNotFoundException;
import br.com.clube_quinze.api.model.appointment.Appointment;
import br.com.clube_quinze.api.model.payment.Plan;
import br.com.clube_quinze.api.model.user.User;
import br.com.clube_quinze.api.model.user.UserPreference;
import br.com.clube_quinze.api.repository.AppointmentRepository;
import br.com.clube_quinze.api.repository.PlanRepository;
import br.com.clube_quinze.api.repository.UserPreferenceRepository;
import br.com.clube_quinze.api.repository.UserRepository;
import br.com.clube_quinze.api.service.user.UserService;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final Clock clock;

    public UserServiceImpl(
            UserRepository userRepository,
            PlanRepository planRepository,
            AppointmentRepository appointmentRepository,
            UserPreferenceRepository userPreferenceRepository,
            Clock clock) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.appointmentRepository = appointmentRepository;
        this.userPreferenceRepository = userPreferenceRepository;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = findUser(userId);
        return buildUserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateUserRequest request) {
        User user = findUser(userId);

        if (!user.getEmail().equalsIgnoreCase(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email já cadastrado");
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setBirthDate(request.birthDate());
        user.setMembershipTier(request.membershipTier());

        Plan plan = null;
        if (request.planId() != null) {
            plan = planRepository.findById(request.planId())
                    .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado"));
        }
        user.setPlan(plan);

        User updated = userRepository.save(user);
        return buildUserProfileResponse(updated);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private UserProfileResponse buildUserProfileResponse(User user) {
        PlanSummary planSummary = Optional.ofNullable(user.getPlan())
                .map(this::toPlanSummary)
                .orElse(null);

        AppointmentResponse nextAppointment = appointmentRepository.findUpcomingByClient(user.getId(), LocalDateTime.now(clock)).stream()
                .findFirst()
                .map(this::toAppointmentResponse)
                .orElse(null);

        List<PreferenceResponse> preferences = userPreferenceRepository.findByUserId(user.getId()).stream()
                .map(this::toPreferenceResponse)
                .toList();

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getBirthDate(),
                user.getMembershipTier(),
                user.getRole(),
                planSummary,
                user.getCreatedAt(),
                user.getLastLogin(),
                nextAppointment,
                preferences);
    }

    private PlanSummary toPlanSummary(Plan plan) {
        return new PlanSummary(plan.getId(), plan.getName(), plan.getDescription(), plan.getPrice(), plan.getDurationMonths());
    }

    private AppointmentResponse toAppointmentResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getClient().getId(),
                appointment.getScheduledAt(),
                appointment.getAppointmentTier(),
                appointment.getStatus(),
                appointment.getServiceType(),
                appointment.getNotes());
    }

    private PreferenceResponse toPreferenceResponse(UserPreference preference) {
        return new PreferenceResponse(
                preference.getId(),
                preference.getPreferenceKey(),
                preference.getPreferenceValue(),
                preference.getCreatedAt(),
                preference.getUpdatedAt());
    }
}