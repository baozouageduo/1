package com.tests.campuslostandfoundsystem.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Slf4j
public class JwtUtils {
    @Value("${Jwt.secretKey}")
    private String secretKey;
    @Value("${Jwt.expiration}")
    private Long expiration;
    @Value("${Jwt.refreshExpiration}")
    private Long refreshExpiration;

    //  生成accessToken
    public String generateAccessToken(String userId, String sessionId) {
        Date currentTime = new Date();
        SecretKey key = generateSecretKey();
        return Jwts.builder()
                .subject(userId)
                .id(UUID.randomUUID().toString())
                .claim("sid", sessionId)
                .issuedAt(currentTime)
                .signWith(key)
                .expiration(new Date(currentTime.getTime()+expiration))
                .claim("tokenKind","accessToken")
                .compact();
    }
    //  生成refreshToken
    public String generateRefreshToken(String userId, String sessionId) {
        Date currentTime = new Date();
        SecretKey key = generateSecretKey();
        return Jwts.builder()
                .subject(userId)
                .id(UUID.randomUUID().toString())
                .claim("sid", sessionId)
                .issuedAt(currentTime)
                .signWith(key)
                .expiration(new Date(currentTime.getTime()+refreshExpiration))
                .claim("tokenKind","refreshToken")
                .compact();
    }
    //  验证token
    public boolean validateToken(String token) {
        try{
            parseToken(token);
            return true;
        }catch (ExpiredJwtException e) {
            log.info("Token已经过期: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    //  解析token
    public Claims parseToken(String token) {
        SecretKey key = generateSecretKey();
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    //  加密
    private SecretKey generateSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    //    方便的get
    public Long getExpiration(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().getTime();
        } catch (ExpiredJwtException e) {
            return 1L;
        }

    }

    public String getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }
    public String getJti(String token) {
        Claims claims = parseToken(token);
        return claims.getId();
    }

    public String getSessionId(String token) {
        Claims claims = parseToken(token);
        return claims.get("sid").toString();
    }
}
