package org.durcit.be.post.service;

import org.durcit.be.post.domain.Post;
import org.durcit.be.post.dto.PostCardResponse;
import org.durcit.be.post.dto.PostRegisterRequest;
import org.durcit.be.post.dto.PostResponse;
import org.durcit.be.post.dto.PostUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    public List<PostResponse> getAllPosts();
    public List<PostResponse> getAllPostsAdmin();
    public List<PostResponse> getAllPostsDeleted();
    public PostResponse getPostById(Long postId);
    public Post getById(Long postId);
    public Post getByIdAdmin(Long postId);
    public PostResponse createPost(PostRegisterRequest postRegisterRequest);
    public void updatePost(Long postId, PostUpdateRequest postUpdateRequest);
    public void deletePost(Long postId);
    public PostResponse getPostWithViewIncrement(Long postId);
    public Page<PostCardResponse> getPostsByPage(Pageable pageable, String category);
    public Page<PostCardResponse> getPostsByFollowedTags(Long memberId, PageRequest pageRequest, String category);
    public Page<PostCardResponse> searchPostsByTag(String tag, PageRequest pageRequest);
    public List<PostCardResponse> getMyPosts(Long memberId);
    public Post recoverPost(Long postId);
    public void deletePostPermanently(Long postId);

    Long getPostsCount();
}
