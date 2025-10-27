package br.com.clube_quinze.api.service.community.impl;

import br.com.clube_quinze.api.dto.common.PageResponse;
import br.com.clube_quinze.api.dto.community.CommentRequest;
import br.com.clube_quinze.api.dto.community.CommentResponse;
import br.com.clube_quinze.api.dto.community.LikeResponse;
import br.com.clube_quinze.api.dto.community.PostRequest;
import br.com.clube_quinze.api.dto.community.PostResponse;
import br.com.clube_quinze.api.exception.BusinessException;
import br.com.clube_quinze.api.exception.ResourceNotFoundException;
import br.com.clube_quinze.api.exception.UnauthorizedException;
import br.com.clube_quinze.api.model.community.CommunityComment;
import br.com.clube_quinze.api.model.community.CommunityLike;
import br.com.clube_quinze.api.model.community.CommunityPost;
import br.com.clube_quinze.api.model.user.User;
import br.com.clube_quinze.api.repository.CommunityCommentRepository;
import br.com.clube_quinze.api.repository.CommunityLikeRepository;
import br.com.clube_quinze.api.repository.CommunityPostRepository;
import br.com.clube_quinze.api.repository.UserRepository;
import br.com.clube_quinze.api.service.community.CommunityService;
import br.com.clube_quinze.api.util.PageUtils;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommunityServiceImpl implements CommunityService {

    private final CommunityPostRepository postRepository;
    private final CommunityCommentRepository commentRepository;
    private final CommunityLikeRepository likeRepository;
    private final UserRepository userRepository;

    public CommunityServiceImpl(
            CommunityPostRepository postRepository,
            CommunityCommentRepository commentRepository,
            CommunityLikeRepository likeRepository,
            UserRepository userRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PostResponse> getFeed(int page, int size, Long authorId) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CommunityPost> postPage = authorId == null
                ? postRepository.findAll(pageable)
                : postRepository.findByAuthorId(authorId, pageable);
        Page<PostResponse> mapped = postPage.map(this::toPostResponse);
        return PageUtils.toResponse(mapped);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        CommunityPost post = findPost(postId);
        return toPostResponse(post);
    }

    @Override
    public PostResponse createPost(Long authorId, PostRequest request) {
        User author = findUser(authorId);

        String title = normalizeAndValidateText(request.title(), "Título é obrigatório");
        String content = normalizeAndValidateText(request.content(), "Conteúdo é obrigatório");

        CommunityPost post = new CommunityPost();
        post.setAuthor(author);
        post.setTitle(title);
        post.setContent(content);
        post.setImageUrl(normalizeOptional(request.imageUrl()));
        post.setImageBase64(normalizeOptional(request.imageBase64()));

        CommunityPost saved = postRepository.save(post);
        return toPostResponse(saved);
    }

    @Override
    public void deletePost(Long postId, Long actorId, boolean privileged) {
        CommunityPost post = findPost(postId);
        if (!privileged && !post.getAuthor().getId().equals(actorId)) {
            throw new UnauthorizedException("Não é permitido remover este post");
        }
        postRepository.delete(post);
    }

    @Override
    public CommentResponse addComment(Long postId, Long authorId, CommentRequest request) {
        CommunityPost post = findPost(postId);
        User author = findUser(authorId);

        String content = normalizeAndValidateText(request.content(), "Conteúdo é obrigatório");

        CommunityComment comment = new CommunityComment();
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setContent(content);

        CommunityComment saved = commentRepository.save(comment);
        return toCommentResponse(saved);
    }

    @Override
    public void deleteComment(Long postId, Long commentId, Long actorId, boolean privileged) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado"));
        if (!comment.getPost().getId().equals(postId)) {
            throw new ResourceNotFoundException("Comentário não encontrado para este post");
        }
        if (!privileged && !comment.getAuthor().getId().equals(actorId)) {
            throw new UnauthorizedException("Não é permitido remover este comentário");
        }
        commentRepository.delete(comment);
    }

    @Override
    public LikeResponse likePost(Long postId, Long userId) {
        CommunityPost post = findPost(postId);
        User user = findUser(userId);

        return likeRepository.findByPostIdAndUserId(postId, userId)
                .map(this::toLikeResponse)
                .orElseGet(() -> {
                    CommunityLike like = new CommunityLike();
                    like.setPost(post);
                    like.setUser(user);
                    CommunityLike saved = likeRepository.save(like);
                    return toLikeResponse(saved);
                });
    }

    @Override
    public void unlikePost(Long postId, Long userId) {
        likeRepository.findByPostIdAndUserId(postId, userId).ifPresent(likeRepository::delete);
    }

    private CommunityPost findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado"));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private PostResponse toPostResponse(CommunityPost post) {
        List<CommentResponse> comments = commentRepository.findByPostId(post.getId()).stream()
                .map(this::toCommentResponse)
                .toList();
        long likeCount = likeRepository.countByPostId(post.getId());
        return new PostResponse(
                post.getId(),
                post.getAuthor().getId(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                post.getImageBase64(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                likeCount,
                comments);
    }

    private CommentResponse toCommentResponse(CommunityComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getAuthor().getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }

    private LikeResponse toLikeResponse(CommunityLike like) {
        return new LikeResponse(
                like.getId(),
                like.getPost().getId(),
                like.getUser().getId(),
                like.getCreatedAt());
    }

    private String normalizeAndValidateText(String value, String errorMessage) {
        String normalized = value == null ? null : value.trim();
        if (normalized == null || normalized.isBlank()) {
            throw new BusinessException(errorMessage);
        }
        return normalized;
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
