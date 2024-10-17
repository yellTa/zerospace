package com.zerospace.zerospace.service.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.Date;

import static com.zerospace.zerospace.Const.Const.*;


@Component
@Slf4j
public class JWTTokenService {

    public String createAcecssToken(String userId) {
        Instant now = Instant.now();
        log.info("key value={}", SECRET_KEY);

        String accessToken = Jwts.builder()
                .setSubject(userId)
                .setIssuer("zerospace")
                .setIssuedAt(Date.from(now))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS))
                .signWith(SignatureAlgorithm.HS256, KEY)
                .compact();

        return accessToken;
    }

    public String createRefreshToken(String userId) {
        Instant now = Instant.now();
        String refreshToken = Jwts.builder()
                .setSubject(userId)
                .setIssuer("zerospace")
                .setIssuedAt(Date.from(now))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_SECONDS))
                .signWith(SignatureAlgorithm.HS256, KEY)
                .compact();

        return refreshToken;
    }

    public String getAccessToken(HttpServletRequest request) {
        String token = request.getHeader(ACCESS_TOKEN_NAME);

        if (token == null || token.isEmpty()) {
            log.warn("Access token is missing");
            return "";
        }

        if (token.length() < 7 || !token.startsWith("Bearer ")) {
            log.warn("Invalid access token format");
            return "";
        }

        return token.substring(7);
    }

    public String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public boolean isTokenValidate(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)  // 서명된 JWT를 파싱하기 위해 parseClaimsJws() 사용
                    .getBody();
            return true;
        } catch (SignatureException e) {
            log.warn("Invalid token signature", e);
            return false;
        } catch (ExpiredJwtException e) {
            log.warn("Token expired", e);
            return false;
        } catch (Exception e) {
            log.warn("Token validation failed", e);
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)  // 서명된 JWT를 파싱하기 위해 parseClaimsJws() 사용
                    .getBody();

            Date expiration = claims.getExpiration();
            return expiration.before(new Date());  // 만료되었으면 true 반환
        } catch (ExpiredJwtException e) {
            return true;  // 만료된 경우 true 반환
        } catch (Exception e) {
            log.warn("Error occurred during token expiration check", e);
            return true;  // 만료되었다고 간주
        }
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public ResponseCookie deleteRefreshToken() {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)  // 쿠키 만료 시간을 0으로 설정하여 삭제
                .sameSite("None")
                .build();
        return refreshTokenCookie;
    }

}
