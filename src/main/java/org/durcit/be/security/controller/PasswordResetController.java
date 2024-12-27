package org.durcit.be.security.controller;

import lombok.RequiredArgsConstructor;
import org.durcit.be.security.dto.PasswordResetRequest;
import org.durcit.be.security.dto.VerifyCodeRequest;
import org.durcit.be.security.service.PasswordResetService;
import org.durcit.be.system.response.ResponseCode;
import org.durcit.be.system.response.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/passwords")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/request")
    public ResponseEntity<ResponseData> requestPasswordReset() {
        passwordResetService.sendVerificationCode();
        return ResponseData.toResponseEntity(ResponseCode.SEND_EMAIL_SUCCESS);
    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseData> verifyCode(@RequestBody VerifyCodeRequest request) {
        boolean verified = passwordResetService.verifyCode(request.getCode());
        if (verified) {
            return ResponseData.toResponseEntity(ResponseCode.VERIFY_EMAIL_SUCCESS);
        } else {
            return ResponseData.toResponseEntity(ResponseCode.VERIFY_EMAIL_FAIL);
        }
    }

    @PostMapping("/change")
    public ResponseEntity<ResponseData> changePassword(@RequestBody PasswordResetRequest request) {
        passwordResetService.changePassword(request);
        return ResponseData.toResponseEntity(ResponseCode.PASSWORD_RESET_SUCCESS);
    }

}
