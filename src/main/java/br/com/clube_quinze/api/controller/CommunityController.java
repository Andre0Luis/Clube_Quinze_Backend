package br.com.clube_quinze.api.controller;

import br.com.clube_quinze.api.dto.common.PageResponse;
import br.com.clube_quinze.api.dto.community.CommentRequest;
import br.com.clube_quinze.api.dto.community.CommentResponse;
import br.com.clube_quinze.api.dto.community.LikeResponse;
import br.com.clube_quinze.api.dto.community.PostRequest;
import br.com.clube_quinze.api.dto.community.PostResponse;
import br.com.clube_quinze.api.exception.UnauthorizedException;
import br.com.clube_quinze.api.model.enumeration.RoleType;
import br.com.clube_quinze.api.security.ClubeQuinzeUserDetails;
import br.com.clube_quinze.api.service.community.CommunityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/community/posts")
@Tag(name = "Comunidade")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostResponse>> listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long authorId) {
        PageResponse<PostResponse> response = communityService.getFeed(page, size, authorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.getPost(postId));
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal ClubeQuinzeUserDetails currentUser,
            @Valid @RequestBody PostRequest request) {
        ClubeQuinzeUserDetails user = requireAuthenticated(currentUser);
        PostResponse created = communityService.createPost(user.getId(), request);
        URI location = URI.create(String.format("/api/v1/community/posts/%d", created.id()));
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal ClubeQuinzeUserDetails currentUser) {
        ClubeQuinzeUserDetails user = requireAuthenticated(currentUser);
        communityService.deletePost(postId, user.getId(), isPrivileged(user));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal ClubeQuinzeUserDetails currentUser,
            @Valid @RequestBody CommentRequest request) {
        ClubeQuinzeUserDetails user = requireAuthenticated(currentUser);
        CommentResponse response = communityService.addComment(postId, user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal ClubeQuinzeUserDetails currentUser) {
        ClubeQuinzeUserDetails user = requireAuthenticated(currentUser);
        communityService.deleteComment(postId, commentId, user.getId(), isPrivileged(user));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<LikeResponse> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal ClubeQuinzeUserDetails currentUser) {
        ClubeQuinzeUserDetails user = requireAuthenticated(currentUser);
        LikeResponse response = communityService.likePost(postId, user.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal ClubeQuinzeUserDetails currentUser) {
        ClubeQuinzeUserDetails user = requireAuthenticated(currentUser);
        communityService.unlikePost(postId, user.getId());
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
