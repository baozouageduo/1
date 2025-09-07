package com.tests.campuslostandfoundsystem.controller;

import com.esotericsoftware.kryo.Registration;
import com.tests.campuslostandfoundsystem.entity.R;
import com.tests.campuslostandfoundsystem.entity.auth.*;
import com.tests.campuslostandfoundsystem.entity.utils.GraphCaptcha;
import com.tests.campuslostandfoundsystem.service.auth.AuthService;
import com.tests.campuslostandfoundsystem.utils.CaptchaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final CaptchaUtils captchaUtils;

    @PostMapping("/register")
    public R<Void> register(@RequestBody RegisterDTO registerDTO){
        authService.register(registerDTO);
        return R.success(null);
    }

    @PostMapping("/login")
    public R<LoginSuccessDTO> login(@RequestBody LoginInfoDTO loginInfoDTO){
        return R.success(authService.login(loginInfoDTO));
    }

    @PostMapping("/logout")
    public R<Void> logout(@RequestBody RefreshTokenInfoDTO refreshTokenInfoDTO){
        authService.logout(refreshTokenInfoDTO);
        return R.success(null);
    }

    @PostMapping("/refreshToken")
    public R<RefreshTokenSuccessDTO> refreshToken(@RequestBody RefreshTokenInfoDTO refreshTokenInfoDTO){
        return R.success(authService.refreshToken(refreshTokenInfoDTO));
    }

    @GetMapping("/GraphCaptcha")
    public R<GraphCaptcha> generateGraphCaptcha(){
        return R.success(authService.generateCaptcha());
    }
}
