package org.durcit.be.post.dto;

import lombok.*;
import org.durcit.be.post.domain.Post;
import org.durcit.be.system.util.TimeAgoUtil;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private String author;
    private Long authorId;
    private Long views;
    private Long likes;
    private Long comments;
    private String userThumbnail;
    private String createdAt;

    @Builder
    public PostResponse(Long id, String title, String content, String author, String userThumbnail, Long likes, Long views, String createdAt, Long authorId, Long comments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.authorId = authorId;
        this.views = views;
        this.createdAt = createdAt;
        this.likes = likes;
        this.comments = comments;
        this.userThumbnail = userThumbnail;
    }

    public static PostResponse fromEntity(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getMember().getNickname())
                .authorId(post.getMember().getId())
                .views(post.getViews())
                .createdAt(TimeAgoUtil.formatElapsedTime(post.getUpdatedAt()))
                .userThumbnail(post.getMember().getProfileImage())
                .comments(post.getComments() != null? (long) post.getComments().size() : 0L)
                .likes(post.getLikes() != null ? (long) post.getLikes().size() : 0L)
                .build();
    }

}
