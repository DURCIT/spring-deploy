package org.durcit.be.security.repository;


import org.durcit.be.security.domian.Member;
import org.durcit.be.security.domian.RefreshToken;

import java.util.Optional;

public interface TokenRepository {

    RefreshToken save(Member member, String token);
    Optional<RefreshToken> findValidRefTokenByToken(String token);
    Optional<RefreshToken> findValidRefTokenByMemberId(Long memberId);
    RefreshToken appendBlackList(RefreshToken token);

}
