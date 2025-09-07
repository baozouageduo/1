package com.tests.campuslostandfoundsystem.service.auth;

import com.tests.campuslostandfoundsystem.entity.auth.*;
import com.tests.campuslostandfoundsystem.entity.utils.GraphCaptcha;

public interface AuthService {
    public void register(RegisterDTO user);
    public LoginSuccessDTO login(LoginInfoDTO dto);
    public void logout(RefreshTokenInfoDTO dto);
    public RefreshTokenSuccessDTO refreshToken(RefreshTokenInfoDTO dto);
    public GraphCaptcha generateCaptcha();
}
