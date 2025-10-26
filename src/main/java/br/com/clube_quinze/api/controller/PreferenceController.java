package br.com.clube_quinze.api.controller;

import br.com.clube_quinze.api.dto.preference.PreferenceRequest;
import br.com.clube_quinze.api.dto.preference.PreferenceResponse;
import br.com.clube_quinze.api.exception.UnauthorizedException;
import br.com.clube_quinze.api.model.enumeration.RoleType;
import br.com.clube_quinze.api.security.ClubeQuinzeUserDetails;
import br.com.clube_quinze.api.service.preference.PreferenceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/preferences")
@Tag(name = "Preferências de Atendimento")
public class PreferenceController {

    private final PreferenceService preferenceService;

    public PreferenceController(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @GetMapping
    public ResponseEntity<List<PreferenceResponse>> listPreferences(
            @AuthenticationPrincipal ClubeQuinzeUserDetails currentUser,
            @RequestParam(required = false) Long userId) {
        ClubeQuinzeUserDetails user = requireAuthenticated(currentUser);
        boolean privileged = isPrivileged(user);
        List<PreferenceResponse> responses = preferenceService.listPreferences(user.getId(), privileged, userId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<PreferenceResponse> upsertPreference(
            @AuthenticationPrincipal ClubeQuinzeUserDetails currentUser,
            @Valid @RequestBody PreferenceRequest request,
            @RequestParam(required = false) Long userId) {
        ClubeQuinzeUserDetails user = requireAuthenticated(currentUser);
        boolean privileged = isPrivileged(user);
        PreferenceResponse response = preferenceService.upsertPreference(user.getId(), privileged, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{preferenceId}")
    public ResponseEntity<PreferenceResponse> updatePreference(
            @PathVariable Long preferenceId,
            @AuthenticationPrincipal ClubeQuinzeUserDetails currentUser,
            @Valid @RequestBody PreferenceRequest request) {
        ClubeQuinzeUserDetails user = requireAuthenticated(currentUser);
        boolean privileged = isPrivileged(user);
        PreferenceResponse response = preferenceService.updatePreference(preferenceId, user.getId(), privileged, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{preferenceId}")
    public ResponseEntity<Void> deletePreference(
            @PathVariable Long preferenceId,
            @AuthenticationPrincipal ClubeQuinzeUserDetails currentUser) {
        ClubeQuinzeUserDetails user = requireAuthenticated(currentUser);
        boolean privileged = isPrivileged(user);
        preferenceService.deletePreference(preferenceId, user.getId(), privileged);
        return ResponseEntity.noContent().build();
    }

    private ClubeQuinzeUserDetails requireAuthenticated(ClubeQuinzeUserDetails currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("Usuário não autenticado");
        }
        return currentUser;
    }

    private boolean isPrivileged(ClubeQuinzeUserDetails currentUser) {
        RoleType role = currentUser.getRole();
        return role == RoleType.CLUB_ADMIN || role == RoleType.CLUB_EMPLOYE;
    }
}
