package br.com.clube_quinze.api.controller;

import br.com.clube_quinze.api.dto.common.PageResponse;
import br.com.clube_quinze.api.dto.feedback.FeedbackAverageResponse;
import br.com.clube_quinze.api.dto.feedback.FeedbackRequest;
import br.com.clube_quinze.api.dto.feedback.FeedbackResponse;
import br.com.clube_quinze.api.exception.UnauthorizedException;
import br.com.clube_quinze.api.model.enumeration.RoleType;
import br.com.clube_quinze.api.security.ClubeQuinzeUserDetails;
import br.com.clube_quinze.api.service.feedback.FeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feedbacks")
@Tag(name = "Feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<FeedbackResponse> submitFeedback(
            @AuthenticationPrincipal ClubeQuinzeUserDetails currentUser,
            @Valid @RequestBody FeedbackRequest request) {
        ClubeQuinzeUserDetails user = requireAuthenticated(currentUser);
        boolean privileged = isPrivileged(user);
        FeedbackResponse response = feedbackService.submitFeedback(user.getId(), privileged, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<PageResponse<FeedbackResponse>> listMyFeedback(
            @AuthenticationPrincipal ClubeQuinzeUserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ClubeQuinzeUserDetails user = requireAuthenticated(currentUser);
        PageResponse<FeedbackResponse> response = feedbackService.getMyFeedback(user.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLUB_EMPLOYE','CLUB_ADMIN')")
    public ResponseEntity<PageResponse<FeedbackResponse>> listFeedback(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<FeedbackResponse> response = feedbackService.getFeedback(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/averages/services")
    @PreAuthorize("hasAnyRole('CLUB_EMPLOYE','CLUB_ADMIN')")
    public ResponseEntity<List<FeedbackAverageResponse>> getAverageByService() {
        return ResponseEntity.ok(feedbackService.getAverageByService());
    }

    @GetMapping("/averages/users/{userId}")
    @PreAuthorize("hasAnyRole('CLUB_EMPLOYE','CLUB_ADMIN')")
    public ResponseEntity<Double> getUserAverage(@PathVariable Long userId) {
        return ResponseEntity.ok(feedbackService.getUserAverage(userId));
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
