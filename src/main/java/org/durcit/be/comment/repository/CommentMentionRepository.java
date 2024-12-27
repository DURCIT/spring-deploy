package org.durcit.be.comment.repository;

import org.durcit.be.comment.domain.CommentMention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentMentionRepository extends JpaRepository<CommentMention, Long> {
    List<CommentMention> findAllByCommentId(Long id);
}
