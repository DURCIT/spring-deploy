package org.durcit.be.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.durcit.be.security.config.JwtConfiguration;
import org.durcit.be.security.repository.TokenRepository;
import org.durcit.be.security.domian.Member;
import org.durcit.be.security.domian.RefreshToken;
import org.durcit.be.security.dto.KeyPair;
import org.durcit.be.security.dto.TokenBody;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenProvider {

//    @Value("${custom.jwt.validation.access}")
//    private Long accessTime;

    private final JwtConfiguration configuration;
    private final TokenRepository refreshTokenRepositoryAdapter;

    public KeyPair generateKeyPair(Member member) {
        String accessToken = issueAccessToken(member.getId(), member.getRole());
        String refreshToken = issueRefreshToken(member.getId(), member.getRole());

        log.info("member.getId() = {}", member.getId());

        refreshTokenRepositoryAdapter.save(member, refreshToken);

        return KeyPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getId().toString())
                .build();
    }

    public String issueAccessToken(Long id, String role) {
        return issue(id, role, configuration.getValidation().getAccess());
    }

    public String issueRefreshToken(Long id, String role) {
        return issue(id, role, configuration.getValidation().getRefresh());
    }

    public RefreshToken validateRefreshToken(Long memberId) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepositoryAdapter.findValidRefTokenByMemberId(memberId);
        return refreshTokenOptional.orElse(null);
    }

    private String issue(Long memberId, String role, Long validTime) {
        return Jwts.builder()
                .subject(memberId.toString())
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + validTime))
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(configuration.getSecret().getAppKey().getBytes());
    }

    public boolean validate(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch ( JwtException e ) {
            log.info("Invalid JWT Token Detected. msg = {}", e.getMessage());
            log.info("TOKEN : {}", token);
        } catch ( IllegalArgumentException e ) {
            log.info("JWT claims String is empty = {}", e.getMessage());
        } catch ( Exception e ) {
            log.error("an error occurred while validating the token. err msg = {}", e.getMessage());
        }

        return false;
    }

    public TokenBody parseJwt(String token) {
        Jws<Claims> parsed = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);
        return new TokenBody(
                Long.parseLong(parsed.getPayload().getSubject()),
                parsed.getPayload().get("role").toString());
    }

}
