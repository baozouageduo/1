package com.tests.campuslostandfoundsystem.service.auth.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tests.campuslostandfoundsystem.dao.StudentsDAO;
import com.tests.campuslostandfoundsystem.dao.UserDAO;
import com.tests.campuslostandfoundsystem.entity.CustomsUserDetail;
import com.tests.campuslostandfoundsystem.entity.auth.*;
import com.tests.campuslostandfoundsystem.entity.enums.exception.AuthResultCodes;
import com.tests.campuslostandfoundsystem.entity.student.Students;
import com.tests.campuslostandfoundsystem.entity.user.UserProfiles;
import com.tests.campuslostandfoundsystem.entity.user.Users;
import com.tests.campuslostandfoundsystem.entity.utils.GraphCaptcha;
import com.tests.campuslostandfoundsystem.exception.AuthException;
import com.tests.campuslostandfoundsystem.exception.UtilsException;
import com.tests.campuslostandfoundsystem.service.auth.AuthService;
import com.tests.campuslostandfoundsystem.service.user.UserService;
import com.tests.campuslostandfoundsystem.utils.CaptchaUtils;
import com.tests.campuslostandfoundsystem.utils.JwtUtils;
import com.tests.campuslostandfoundsystem.utils.RedisStoreTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtUtils jwtUtils;
    private final UserDAO  userDAO;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaUtils captchaUtils;
    private final AuthenticationManager authenticationManager;
    private final RedisStoreTokenUtils  redisStoreTokenUtils;
    private final StudentsDAO  studentsDAO;
    private final UserService userService;
    @Operation(summary = "用户注册")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterDTO user) {
        try{
//         检查username是否为空
            if(StringUtils.isBlank(user.getUsername())){
                throw new AuthException(AuthResultCodes.AUTHENTICATING_EMPTY, "用户名不能为空");
            }
//         检查password是否为空
            if(StringUtils.isBlank(user.getPassword())){
                throw new AuthException(AuthResultCodes.AUTHENTICATING_EMPTY, "密码不能为空");
            }
//         加密密码
            user.setPassword(passwordEncoder.encode(user.getPassword()));
//          默认注册为学生
            Students stUser = Students.builder()
                    .name(user.getUsername())
                    .password(user.getPassword())
                    .build();
            studentsDAO.insert(stUser);
//          尝试写入user数据库
            Users rigisterUsers = Users.builder()
                                       .username(user.getUsername())
                                       .password(user.getPassword())
                                       .createBy("unknown")
                                       .updateBy("unknown")
                                       .build();
            userDAO.insert(rigisterUsers);

//          维护user_profile表
            userDAO.insertUserProfile(UserProfiles.builder()
                            .userId(rigisterUsers.getId())
                            .profileId(stUser.getId())
                            .profileType("STUDENT")
                    .build());
        }catch(Exception e){
            throw new AuthException(AuthResultCodes.REGISTER_FAILED, e.getMessage(), e);
        }
    }

    @Operation(summary = "用户登录")
    @Override
    public LoginSuccessDTO login(LoginInfoDTO dto) {
       try{
//         检查username是否为空
            if(StringUtils.isBlank(dto.getUsername())){
                throw new AuthException(AuthResultCodes.AUTHENTICATING_EMPTY, "用户名不能为空");
            }
//         检查password是否为空
           if(StringUtils.isBlank(dto.getPassword())){
               throw new AuthException(AuthResultCodes.AUTHENTICATING_EMPTY, "密码不能为空");
           }
//         图形验证码是否为空
           if(StringUtils.isBlank(dto.getGraphCaptchaCode())){
               throw new AuthException(AuthResultCodes.AUTHENTICATING_EMPTY, "验证码不能为空");
           }
//         检查验证码是否正确
           if(!captchaUtils.validateGraphCaptcha( dto.getGraphCaptchaKey(),dto.getGraphCaptchaCode())){
               throw new AuthException(AuthResultCodes.AUTHENTICATING_CAPTCHA_ERROR, "验证码错误");
           }
//         检查是否在数据库中是否存在该用户
           QueryWrapper<Users> qw = new QueryWrapper<>();
           qw.eq("username", dto.getUsername())
                        .eq("is_deleted", 0);
           Users user=userDAO.selectOne(qw);
           if(user==null){
               throw new AuthException(AuthResultCodes.AUTHENTICATING_USER_NOT_EXIST, "用户不存在");
           }
//         SecurityContextHolder处理
           Authentication authentication= authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
           );
           if(authentication==null){
               throw new AuthException(AuthResultCodes.AUTHENTICATING_EMPTY, "authentication为空");
           }
           SecurityContextHolder.getContext().setAuthentication(authentication);
//            生成token
           String sid= UUID.randomUUID().toString();
           String AT = jwtUtils.generateAccessToken(user.getId().toString(),sid);
           String RT = jwtUtils.generateRefreshToken(user.getId().toString(),sid);
//         保存token到redis
           redisStoreTokenUtils.saveTokens(user.getId().toString(), AT, RT);
           return new  LoginSuccessDTO(AT,RT);
       }catch(Exception e){
           throw new AuthException(AuthResultCodes.LOGIN_ERROR, e.getMessage(), e);
       }
    }

    @Operation(summary = "用户登出")
    @Override
    public void logout(RefreshTokenInfoDTO dto) {
        try{
//         验证RT是否有效
           if(!jwtUtils.validateToken(dto.getRefreshToken())){
               throw  new UtilsException(AuthResultCodes.TOKEN_INVALID, "RT无效");
           }
//         验证RT是否对应用户
            CustomsUserDetail nowUserInfo = userService.getUserInfo();
            if(!nowUserInfo.getUserId().equals(jwtUtils.getUserId(dto.getRefreshToken()))){
               throw  new UtilsException(AuthResultCodes.TOKEN_INVALID, "RT与用户不对应");
           }
//         从redis中删除AT、RT
            redisStoreTokenUtils.deleteTokenByUserId(dto.getUserId());
//         拉黑AT
            redisStoreTokenUtils.addBlackListBySid(jwtUtils.getSessionId(dto.getRefreshToken()));
        }catch(Exception e){
           throw new AuthException(AuthResultCodes.LOGOUT_ERROR, e.getMessage(), e);
        }
    }

    @Operation(summary = "刷新token")
    @Override
    public RefreshTokenSuccessDTO refreshToken(RefreshTokenInfoDTO dto) {
      try{
          // 验证RT是否有效
          if(!jwtUtils.validateToken(dto.getRefreshToken())){
              throw  new UtilsException(AuthResultCodes.TOKEN_INVALID, "RT无效");
          }
//        验证RT是否对应用户
          CustomsUserDetail nowUserInfo = userService.getUserInfo();
          if(!nowUserInfo.getUserId().equals(jwtUtils.getUserId(dto.getRefreshToken()))){
              throw  new UtilsException(AuthResultCodes.TOKEN_INVALID, "RT与用户不对应");
          }
//        检查是否在黑名单中
          if(redisStoreTokenUtils.isBlackListBySid(jwtUtils.getSessionId(dto.getRefreshToken()))){
              throw  new AuthException(AuthResultCodes.TOKEN_INVALID, "RT已在黑名单中");
          }
//        生成并返回AT
          String AT=jwtUtils.generateAccessToken(dto.getUserId(),jwtUtils.getSessionId(dto.getRefreshToken()));
          return new RefreshTokenSuccessDTO(AT);
      }catch(Exception e){
         throw new AuthException(AuthResultCodes.REFRESH_TOKEN_ERROR, e.getMessage(), e);
      }
    }

    @Override
    public GraphCaptcha generateCaptcha() {
        GraphCaptcha graphCaptcha = captchaUtils.generateCaptcha();
        graphCaptcha.setCaptchaCode("");
        return graphCaptcha;
    }
}
