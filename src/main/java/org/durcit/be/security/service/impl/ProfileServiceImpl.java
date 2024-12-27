package org.durcit.be.security.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.durcit.be.security.domian.Member;
import org.durcit.be.security.dto.BioRequest;
import org.durcit.be.security.dto.MemberProfileResponse;
import org.durcit.be.security.dto.NicknameRequest;
import org.durcit.be.security.repository.MemberRepository;
import org.durcit.be.security.service.MemberService;
import org.durcit.be.security.service.ProfileService;
import org.durcit.be.security.util.SecurityUtil;
import org.durcit.be.system.exception.auth.ExistingNicknameException;
import org.durcit.be.system.exception.auth.MemberNotFoundException;
import org.durcit.be.system.util.TimeAgoUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

import static org.durcit.be.system.exception.ExceptionMessage.EXISTING_NICKNAME_ERROR;
import static org.durcit.be.system.exception.ExceptionMessage.MEMBER_NOT_FOUND_ERROR;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public MemberProfileResponse getMemberProfile(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND_ERROR));

        return MemberProfileResponse.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .isVerified(member.isVerified())
                .role(member.getRole())
                .provider(member.getProvider())
                .bio(member.getBio())
                .signedAt(TimeAgoUtil.formatElapsedTime(member.getSignedAt()))
                .build();
    }

    public MemberProfileResponse getCurrentMemberProfile() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        log.info("memberId = {}", memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND_ERROR));

        return MemberProfileResponse.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .isVerified(member.isVerified())
                .role(member.getRole())
                .provider(member.getProvider())
                .bio(member.getBio())
                .build();
    }

    public boolean isDuplicateNickname(NicknameRequest nicknameRequest) {
        log.info("nicknameRequest.getNickname() = {}", nicknameRequest.getNickname());
        return memberRepository.existsByNicknameIgnoreCase(nicknameRequest.getNickname().trim());
    }




    // 메서드 기능: 입력 닉네임을 받아서 변경하는 기능이다.
    // 예외: 같은 닉네임이 있으면 예외를 던진다. 또한 내부 메서드를 활용했고 해당 멤버가 아니면 예외를 던진다.
    // 반환: 저장한 엔티티를 MemberProfileResponse 로 변환하여 반환한다.
    @Transactional
    public MemberProfileResponse updateNickName(NicknameRequest nicknameRequest) {

        Long memberId = SecurityUtil.getCurrentMemberId();
        log.info("memberId = {}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND_ERROR));

        String nickname = nicknameRequest.getNickname();

        if (isDuplicateNickname(nicknameRequest)) {
            throw new ExistingNicknameException(EXISTING_NICKNAME_ERROR);
        }

        member.setNickname(nickname);
        Member savedMember = memberRepository.save(member);


        return MemberProfileResponse.builder()
                .email(savedMember.getEmail())
                .username(savedMember.getUsername())
                .nickname(savedMember.getNickname())
                .profileImage(savedMember.getProfileImage())
                .isVerified(savedMember.isVerified())
                .role(savedMember.getRole())
                .provider(savedMember.getProvider())
                .build();
    }



    // 메서드 기능: 입력 자기소개(Bio)를 받아서 변경하는 기능이다.
    // 예외: 해당하는 멤버가 아니면 예외를 던진다. 내부 메서드를 활용했다.
    // 반환: 저장한 엔티티를 MemberProfileResponse 로 변환하여 반환한다.
    @Transactional
    public MemberProfileResponse updateBio(BioRequest bioRequest) {


        Long memberId = SecurityUtil.getCurrentMemberId();
        log.info("memberId = {}", memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND_ERROR));

        String bio = bioRequest.getBio();

        member.setBio(bio);
        Member savedMember = memberRepository.save(member);


        return MemberProfileResponse.builder()
                .email(savedMember.getEmail())
                .username(savedMember.getUsername())
                .nickname(savedMember.getNickname())
                .profileImage(savedMember.getProfileImage())
                .isVerified(savedMember.isVerified())
                .role(savedMember.getRole())
                .provider(savedMember.getProvider())
                .bio(savedMember.getBio())
                .build();
    }


    @Override
    public String getMemberBio() {
        Member member = memberService.getById(SecurityUtil.getCurrentMemberId());
        return member.getBio();
    }
}
