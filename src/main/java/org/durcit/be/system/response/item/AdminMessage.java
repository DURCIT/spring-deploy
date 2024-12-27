package org.durcit.be.system.response.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminMessage {

    public static String GET_ADMIN_LOG_SUCCESS = "SUCCESS - 로그 조회 성공";
    public static String RECOVER_POST_SUCCESS = "SUCCESS - 게시물 복구 성공";
    public static String UPDATE_ROLE_SUCCESS = "SUCCESS - 역할 수정 성공";

    public static String UPDATE_USER_BLOCK_STATUS_SUCCESS = "SUCCESS - 유저 블락 상태 변경 성공";
    public static String DELETE_USER_PERMANENT_SUCCESS = "SUCCESS - 유저 삭제 성공";

}
