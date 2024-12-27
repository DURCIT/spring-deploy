package org.durcit.be.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.durcit.be.comment.domain.Comment;
import org.durcit.be.system.util.TimeAgoUtil;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCardResponse {

    private Long id;
    private String content;
    private String authorNickname;
    private String userThumbnail;
    private Long authorId;
    private Long parentId;
    private Long postId;
    private String createdAt;
    private List<String> mentionList;

    public static CommentCardResponse from(Comment comment) {
        return CommentCardResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorNickname(comment.getAuthor().getNickname())
                .userThumbnail(comment.getAuthor().getProfileImage())
                .authorId(comment.getAuthor().getId())
                .postId(comment.getPost().getId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .mentionList(comment.getMentionList() != null
                        ? comment.getMentionList().stream()
                        .map(mention -> mention.getMember().getNickname())
                        .toList()
                        : List.of())
                .createdAt(TimeAgoUtil.formatElapsedTime(comment.getUpdatedAt()))
                .build();
    }

}
