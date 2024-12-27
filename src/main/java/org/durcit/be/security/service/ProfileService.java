package org.durcit.be.security.service;

import org.durcit.be.security.dto.BioRequest;
import org.durcit.be.security.dto.MemberProfileResponse;
import org.durcit.be.security.dto.NicknameRequest;

public interface ProfileService {

    public MemberProfileResponse getMemberProfile(Long userId);
    public MemberProfileResponse getCurrentMemberProfile();
    public MemberProfileResponse updateNickName(NicknameRequest nicknameRequest);
    public boolean isDuplicateNickname(NicknameRequest nicknameRequest);
    public MemberProfileResponse updateBio(BioRequest bioRequest);

    String getMemberBio();
}
