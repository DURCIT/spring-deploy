package org.durcit.be.postsTag.repository;

import org.durcit.be.post.domain.Post;
import org.durcit.be.postsTag.domain.PostsTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PostsTagRepository extends JpaRepository<PostsTag, Long> {

    List<PostsTag> findByContents(String contents);

    @Query("SELECT p FROM Post as p WHERE p.id = :postId")  // postId 를 통해 jpql로 Post 를 받아온다.
    public Post findPostByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM PostsTag pt WHERE pt.post.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    List<PostsTag> findByContentsContaining(String query);
}
