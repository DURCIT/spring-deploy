package org.durcit.be.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.durcit.be.security.domian.RefreshToken;
import org.durcit.be.security.dto.RefreshTokenRequest;
import org.durcit.be.security.repository.MemberRepository;
import org.durcit.be.security.repository.VerificationTokenRepository;
import org.durcit.be.security.domian.Member;
import org.durcit.be.security.domian.VerificationToken;
import org.durcit.be.security.dto.KeyPair;
import org.durcit.be.security.dto.LoginRequest;
import org.durcit.be.security.dto.RegisterRequest;
import org.durcit.be.security.repository.adapter.RefreshTokenRepositoryAdapter;
import org.durcit.be.security.util.ProfileImageUtil;
import org.durcit.be.security.util.SecurityUtil;
import org.durcit.be.system.exception.auth.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.durcit.be.system.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepositoryAdapter refreshTokenRepository;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void register(RegisterRequest request, String profileImageUrl) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException(DUPLICATE_EMAIL_ERROR);
        }

        if (profileImageUrl == null) {
            profileImageUrl = ProfileImageUtil.generateRandomProfileImage(request.getEmail());
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname().isBlank() ? generateUniqueNickname() : request.getNickname())
                .isVerified(false)
                .profileImage(profileImageUrl)
                .build();

        memberRepository.save(member);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, member);
        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(member.getEmail(), token);
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new NotValidTokenException(NOT_VALID_TOKEN_ERROR));

        Member member = verificationToken.getMember();
        member.setVerified(true);
        memberRepository.save(member);
        tokenRepository.delete(verificationToken);
    }

    private String generateUniqueNickname() {
        String nickname;
        do {
            nickname = NickNameFactory.createNickname();
        } while (memberRepository.existsByNickname(nickname));
        return nickname;
    }

    @Transactional
    public KeyPair login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidUsernamePasswordException(INVALID_USERNAME_PASSWORD_ERROR));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new InvalidUsernamePasswordException(INVALID_USERNAME_PASSWORD_ERROR);
        }

        if (!member.isVerified()) {
            throw new EmailNotVerifiedException(EMAIL_NOT_VERIFIED_ERROR);
        }

        if (member.isBlocked()) {
            throw new MemberBlockedException(MEMBER_BLOCKED_ERROR);
        }
        return jwtTokenProvider.generateKeyPair(member);
    }

    @Transactional
    public KeyPair adminLogin(LoginRequest request) {
        log.info("request.getEmail() = {}", request.getEmail());
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidUsernamePasswordException(INVALID_USERNAME_PASSWORD_ERROR));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new InvalidUsernamePasswordException(INVALID_USERNAME_PASSWORD_ERROR);
        }

        if (!member.isVerified()) {
            throw new EmailNotVerifiedException(EMAIL_NOT_VERIFIED_ERROR);
        }

        if (member.isBlocked()) {
            throw new MemberBlockedException(MEMBER_BLOCKED_ERROR);
        }

        if (!member.getRole().equals("ADMIN")) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS_ERROR);
        }
        return jwtTokenProvider.generateKeyPair(member);
    }


    @Transactional
    public void addRefreshTokenToBlacklist(RefreshTokenRequest refreshToken) {
        RefreshToken validRefTokenByMemberId = refreshTokenRepository.findValidRefTokenByMemberId(refreshToken.getMemberId())
                .orElseThrow(() -> new NotValidTokenException(NOT_VALID_TOKEN_ERROR));

        refreshTokenRepository.appendBlackList(validRefTokenByMemberId);
    }
}
