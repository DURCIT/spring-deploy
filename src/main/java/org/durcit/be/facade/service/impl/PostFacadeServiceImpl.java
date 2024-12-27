package org.durcit.be.facade.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.durcit.be.comment.dto.CommentCardResponse;
import org.durcit.be.comment.service.CommentService;
import org.durcit.be.facade.dto.PostCombinedResponse;
import org.durcit.be.facade.dto.PostRegisterCombinedRequest;
import org.durcit.be.facade.dto.PostUpdateCombinedRequest;
import org.durcit.be.facade.service.PostFacadeService;
import org.durcit.be.follow.service.TagFollowService;
import org.durcit.be.post.dto.EmojiResponse;
import org.durcit.be.post.dto.PostEmojisResponse;
import org.durcit.be.post.dto.PostResponse;
import org.durcit.be.post.service.EmojiService;
import org.durcit.be.post.service.PostService;
import org.durcit.be.postsTag.dto.PostsTagResponse;
import org.durcit.be.postsTag.service.PostsTagService;
import org.durcit.be.upload.dto.UploadResponse;
import org.durcit.be.upload.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostFacadeServiceImpl implements PostFacadeService {

    private final PostService postService;
    private final UploadService uploadService;
    private final PostsTagService postsTagService;
    private final EmojiService emojiService;
    private final CommentService commentService;
    private final TagFollowService tagFollowService;
    // 태그 서비스

    // 서비스 레이어들의 결합도를 낮추기 위해 사용
    // uploadService -> 파일과 json을 같이 다루기 어렵기 때문에 따로 요청
    // postService, postTagService -> 조합 Dto 사용하여 facade 패턴으로 해당 서비스에서 같이 처리

    @Transactional
    public Long registerPost(PostRegisterCombinedRequest request) {
        PostResponse post = postService.createPost(request.getPostRegisterRequest());
        postsTagService.createPostsTag(request.getPostsTagRegisterRequest(), post.getId());
        return post.getId();
    }

    @Transactional
    public PostCombinedResponse getPostById(Long postId, Long memberId) {
        PostResponse postById = postService.getPostById(postId);
        List<PostsTagResponse> postsTagResponseListByPostId = postsTagService.getPostsTagResponseListByPostId(postId);
        if (memberId != null) {
            postsTagResponseListByPostId = tagFollowService.processTagsWithFollowStatus(postsTagResponseListByPostId, memberId);
        }
        List<UploadResponse> imagesByPostId = uploadService.getImagesByPostId(postId);
        PostEmojisResponse postEmojis = emojiService.getPostEmojis(postId);
        List<CommentCardResponse> comments = commentService.getCommentsByPostId(postId);
        return PostCombinedResponse.builder()
                .post(postById)
                .tags(postsTagResponseListByPostId)
                .uploads(imagesByPostId)
                .emojis(postEmojis)
                .comments(comments)
                .build();
    }

    @Transactional
    public void updatePosts(PostUpdateCombinedRequest request, Long postId) {
        postService.updatePost(postId, request.getPostUpdateRequest());
        postsTagService.updatePostsTag(request.getPostsTagRegisterRequests(), postId);
    }

    @Transactional
    public void deletePosts(Long postId) {
        postService.deletePost(postId);
    }

    @Transactional
    public void deletePostsByMemberId(Long memberId) {
        commentService.deleteCommentWithMemberId(memberId);
    }


}
