package com.tests.campuslostandfoundsystem.utils;

import com.tests.campuslostandfoundsystem.dao.UserDAO;
import com.tests.campuslostandfoundsystem.entity.enums.exception.AuthResultCodes;
import com.tests.campuslostandfoundsystem.entity.enums.exception.UtilsResultCodes;
import com.tests.campuslostandfoundsystem.entity.utils.RedisTokenInfo;
import com.tests.campuslostandfoundsystem.exception.AuthException;
import com.tests.campuslostandfoundsystem.exception.UtilsException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
@Component
@RequiredArgsConstructor
public class RedisStoreTokenUtils {
    private final UserDAO  userDAO;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtils jwtUtils;
    //   便利的get方法
    private String AUTH_TOKEN_PREFIX = "auth:";


    //   存token--单点登录
    @Transactional(rollbackFor = Exception.class)
    public void saveTokens(String userId, String accessToken, String refreshToken) {
        String atSid = jwtUtils.getSessionId(accessToken);
        String rtSid = jwtUtils.getSessionId(refreshToken);
        if (!atSid.equals(rtSid)) {
            throw new AuthException(AuthResultCodes.TOKEN_GENERATE_ERROR, "AT和RT的sessionId不一致,拒绝保存");
        }
        long expireTime = jwtUtils.getExpiration(refreshToken);
        if (expireTime <= 0) {
            return;
        }

        String sidKey = getSidKey(rtSid);
        Map<String, String> sessionData = Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        redisTemplate.opsForHash().putAll(sidKey, sessionData);
        redisTemplate.expire(sidKey, expireTime, TimeUnit.MILLISECONDS);

        String usernameKey = getUserIdKey(userId);
        redisTemplate.opsForValue().set(usernameKey, rtSid, expireTime, TimeUnit.MILLISECONDS);
    }


    //  获取token
    @Transactional(rollbackFor = Exception.class)
    public RedisTokenInfo getTokensByUsername(String userId) {
        String sid = (String) redisTemplate.opsForValue().get(getUserIdKey(userId));
        if (sid == null) {
            return null;
        }
        String sidKey = getSidKey(sid);
        Map<Object, Object> sessionData = redisTemplate.opsForHash().entries(sidKey);
        String accessToken = (String) sessionData.get("accessToken");
        String refreshToken = (String) sessionData.get("refreshToken");
        RedisTokenInfo redisTokenInfo = new RedisTokenInfo();
        redisTokenInfo.setAccessToken(accessToken);
        redisTokenInfo.setRefreshToken(refreshToken);
        redisTokenInfo.setSid(sid);
        return redisTokenInfo;
    }

    //   查看RT是否存在redis
    public boolean isRefreshTokenExists(String refreshToken) {
        String sid = jwtUtils.getSessionId(refreshToken);
        if (sid == null) {
            return false;
        }
        String sidKey = getSidKey(sid);
        Map<Object, Object> sessionData = redisTemplate.opsForHash().entries(sidKey);
        String rt = (String) sessionData.get("refreshToken");
        return rt != null;
    }

    //   查看AT是否存在redis
    public boolean isAccessTokenExists(String accessToken) {
        String sid = jwtUtils.getSessionId(accessToken);
        if (sid == null) {
            return false;
        }
        String sidKey = getSidKey(sid);
        Map<Object, Object> sessionData = redisTemplate.opsForHash().entries(sidKey);
        String at = (String) sessionData.get("accessToken");
        return at != null;
    }


    //  新删除token
    public void deleteTokenByUserId(String userId) {
        String sid = (String) redisTemplate.opsForValue().get(getUserIdKey(userId));
        if (sid != null) {
            redisTemplate.delete(getSidKey(sid));
        }
        redisTemplate.delete(getUserIdKey(userId));
    }


    //   从redis中拉黑
    public void addBlackListBySid(String sid) {
        try {
            if (sid != null) {
                String sidKey = getSidKey(sid);
                Map<Object, Object> sessionData = redisTemplate.opsForHash().entries(sidKey);
                String accessToken = (String) sessionData.get("accessToken");
                Long expire = jwtUtils.getExpiration(accessToken);
                redisTemplate.opsForValue().set(getBlackListKey(sid), 1, expire, TimeUnit.MILLISECONDS);
            } else {
                throw new UtilsException(UtilsResultCodes.GET_JTI_EXCEPTION, "sid为空");
            }
        } catch (Exception e) {
            throw new UtilsException(UtilsResultCodes.GET_JTI_EXCEPTION, "获取sid异常：" + e.getMessage());
        }

    }

    //
    public void addBlackListByUsername(String userId) {
        String sid = (String) redisTemplate.opsForValue().get(getUserIdKey(userId));
        try {
            if (sid != null) {
                String sidKey = getSidKey(sid);
//                Long expire = redisTemplate.getExpire(sidKey, TimeUnit.MILLISECONDS);
                Map<Object, Object> sessionData = redisTemplate.opsForHash().entries(sidKey);
                String accessToken = (String) sessionData.get("accessToken");
                Long expire = jwtUtils.getExpiration(accessToken);
                redisTemplate.opsForValue().set(getBlackListKey(sid), 1, expire, TimeUnit.MILLISECONDS);
            } else {
                throw new UtilsException(UtilsResultCodes.GET_JTI_EXCEPTION, "sid为空");
            }
        } catch (Exception e) {
            throw new UtilsException(UtilsResultCodes.GET_JTI_EXCEPTION, "获取sid异常：" + e.getMessage());
        }
    }


    //   检测是否在黑名单
    public boolean isBlackListBySid(String sid) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(getBlackListKey(sid)));
    }



    //  判断RT是否合法、是否存在redis、是否在redis黑名单、RT是否可用
    public boolean isRefreshTokenValid(String refreshToken) {
        String sid = jwtUtils.getSessionId(refreshToken);
        if (sid == null) {
            return false;
        }
        if (!jwtUtils.validateToken(refreshToken)) {
            return false;
        }
        String jtiKey = getJtiKey(sid);
        if (!isRefreshTokenExists(refreshToken)) {
            return false;
        }
        if (isBlackListBySid(sid)) {
            return false;
        }
        if (userDAO.selectById(jwtUtils.getUserId(refreshToken)) == null) {
            return false;
        }
        return true;
    }

    //  根据username获取RT
    public String getRefreshTokenByUsername(String username) {
        try {
            RedisTokenInfo tokens = getTokensByUsername(username);
            if (tokens == null) {
                return null;
            }
            String refreshToken = tokens.getRefreshToken();
            if (refreshToken == null) {
                return null;
            }
            return refreshToken;
        } catch (Exception e) {
            throw new UtilsException(UtilsResultCodes.GET_TOKEN_EXCEPTION, "获取RT异常：" + e.getMessage());
        }

    }
 


    public String getRefreshTokenKey(String refreshToken) {
        return AUTH_TOKEN_PREFIX +"refreshToken:"+ refreshToken;
    }
    public String getAccessTokenKey(String accessToken) {
        return AUTH_TOKEN_PREFIX +"accessToken:"+ accessToken;
    }
    public String getUserIdKey(String userId) {
        return AUTH_TOKEN_PREFIX +"userId:"+ userId;
    }

    public String getJtiKey(String jti) {
        return AUTH_TOKEN_PREFIX + "jti:" + jti;
    }

    public String getSidKey(String sid) {
        return AUTH_TOKEN_PREFIX + "sid:" + sid;
    }
    public String getBlackListKey(String token) {
        return AUTH_TOKEN_PREFIX +"blackList:"+ token;
    }
    public String getLoginFailCountKey(String username) {
        return AUTH_TOKEN_PREFIX +"loginFailCount:"+ username;
    }
}

