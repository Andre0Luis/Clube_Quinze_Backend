package br.com.clube_quinze.api.service.preference.impl;

import br.com.clube_quinze.api.dto.preference.PreferenceRequest;
import br.com.clube_quinze.api.dto.preference.PreferenceResponse;
import br.com.clube_quinze.api.exception.BusinessException;
import br.com.clube_quinze.api.exception.ResourceNotFoundException;
import br.com.clube_quinze.api.exception.UnauthorizedException;
import br.com.clube_quinze.api.model.user.User;
import br.com.clube_quinze.api.model.user.UserPreference;
import br.com.clube_quinze.api.repository.UserPreferenceRepository;
import br.com.clube_quinze.api.repository.UserRepository;
import br.com.clube_quinze.api.service.preference.PreferenceService;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreferenceServiceImpl implements PreferenceService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    public PreferenceServiceImpl(UserRepository userRepository, UserPreferenceRepository userPreferenceRepository) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PreferenceResponse> listPreferences(Long actorId, boolean privileged, Long userId) {
        Long ownerId = resolveTargetUser(actorId, privileged, userId);
        return userPreferenceRepository.findByUserId(ownerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PreferenceResponse upsertPreference(Long actorId, boolean privileged, Long userId, PreferenceRequest request) {
        Long ownerId = resolveTargetUser(actorId, privileged, userId);
        User owner = findUser(ownerId);

        UserPreference preference = userPreferenceRepository
                .findByUserIdAndPreferenceKey(ownerId, request.key())
                .orElseGet(() -> {
                    UserPreference created = new UserPreference();
                    created.setUser(owner);
                    return created;
                });

        preference.setPreferenceKey(request.key());
        preference.setPreferenceValue(request.value());

        UserPreference saved = userPreferenceRepository.save(preference);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public PreferenceResponse updatePreference(Long preferenceId, Long actorId, boolean privileged, PreferenceRequest request) {
        UserPreference preference = findPreference(preferenceId);
        enforceOwnership(preference, actorId, privileged);

        if (!Objects.equals(preference.getPreferenceKey(), request.key())) {
            boolean keyInUse = userPreferenceRepository
                    .findByUserIdAndPreferenceKey(preference.getUser().getId(), request.key())
                    .filter(existing -> !existing.getId().equals(preference.getId()))
                    .isPresent();
            if (keyInUse) {
                throw new BusinessException("Já existe uma preferência cadastrada com essa chave");
            }
        }

        preference.setPreferenceKey(request.key());
        preference.setPreferenceValue(request.value());

        UserPreference saved = userPreferenceRepository.save(preference);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deletePreference(Long preferenceId, Long actorId, boolean privileged) {
        UserPreference preference = findPreference(preferenceId);
        enforceOwnership(preference, actorId, privileged);
        userPreferenceRepository.delete(preference);
    }

    private Long resolveTargetUser(Long actorId, boolean privileged, Long userId) {
        if (privileged) {
            return userId != null ? userId : actorId;
        }
        if (userId != null && !userId.equals(actorId)) {
            throw new UnauthorizedException("Não é permitido acessar preferências de outro usuário");
        }
        return actorId;
    }

    private void enforceOwnership(UserPreference preference, Long actorId, boolean privileged) {
        if (privileged) {
            return;
        }
        if (!preference.getUser().getId().equals(actorId)) {
            throw new UnauthorizedException("Não é permitido manipular esta preferência");
        }
    }

    private UserPreference findPreference(Long id) {
        return userPreferenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Preferência não encontrada"));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private PreferenceResponse toResponse(UserPreference preference) {
        return new PreferenceResponse(
                preference.getId(),
                preference.getPreferenceKey(),
                preference.getPreferenceValue(),
                preference.getCreatedAt(),
                preference.getUpdatedAt());
    }
}