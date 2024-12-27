package org.durcit.be.chat.repository;

import org.durcit.be.chat.domain.ChatRoom;
import org.durcit.be.security.domian.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByMember_IdAndOpponent_Id(Long memberId, Long opponentId);

    @Query("SELECT c FROM ChatRoom c WHERE (c.member.id = :userId AND c.opponent.id = :opponentId) OR (c.member.id = :opponentId AND c.opponent.id = :userId)")
    Optional<ChatRoom> findByMemberIds(Long userId, Long opponentId);

    @Query("SELECT c FROM ChatRoom c WHERE c.member.id = :memberId")
    List<ChatRoom> findByMemberId(Long memberId);
    @Query("SELECT c FROM ChatRoom c WHERE c.opponent.id = :opponentId")
    List<ChatRoom> findByOpponentId(Long opponentId);


    @Query("SELECT c FROM ChatRoom c WHERE c.member.id = :memberId OR c.opponent.id = :memberId")
    List<ChatRoom> findAllByMemberId(Long memberId);

    @Modifying
    @Query("DELETE FROM ChatRoom c WHERE c.member = :member OR c.opponent = :member")
    void deleteChatRoomsByMember(@Param("member") Member member);
}
