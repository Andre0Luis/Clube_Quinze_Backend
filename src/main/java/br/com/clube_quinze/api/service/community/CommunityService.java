package br.com.clube_quinze.api.service.community;

import br.com.clube_quinze.api.dto.community.CommentRequest;
import br.com.clube_quinze.api.dto.community.CommentResponse;
import br.com.clube_quinze.api.dto.community.LikeResponse;
import br.com.clube_quinze.api.dto.community.PostRequest;
import br.com.clube_quinze.api.dto.community.PostResponse;
import br.com.clube_quinze.api.dto.common.PageResponse;

public interface CommunityService {

    PageResponse<PostResponse> getFeed(int page, int size, Long authorId);

    PostResponse getPost(Long postId);

    PostResponse createPost(Long authorId, PostRequest request);

    void deletePost(Long postId, Long actorId, boolean privileged);

    CommentResponse addComment(Long postId, Long authorId, CommentRequest request);

    void deleteComment(Long postId, Long commentId, Long actorId, boolean privileged);

    LikeResponse likePost(Long postId, Long userId);

    void unlikePost(Long postId, Long userId);
}
