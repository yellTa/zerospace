package com.zerospace.zerospace.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.Date;

import static com.zerospace.zerospace.Const.Const.*;


@Component
public class JWTTokenService {


    public String createAcecssToken(String userId) {
        Instant now = Instant.now();
        String accessToken = Jwts.builder()
                .setSubject(userId)
                .setIssuer("zerospace")
                .setIssuedAt(Date.from(now))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        return accessToken;
    }

    public String createRefreshToken(String userId) {
        Instant now = Instant.now();
        String refreshToken = Jwts.builder()
                .setSubject(userId)
                .setIssuer("zeosapce")
                .setIssuedAt(Date.from(now))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_SECONDS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        return refreshToken;
    }

    public String getAccessToken(HttpServletRequest request) {
        String token = request.getHeader(ACCESS_TOKEN_NAME);
        if(token ==null)return "";
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

    public boolean isTokenValidate(String token){
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJwt(token)
                    .getBody();
            return true;
        }catch(SignatureException e){
            return false;
        }
    }

    public boolean isTokenExpired(String token){
        try{
            Claims claims = Jwts.parser()
                    .parseClaimsJwt(token)
                    .getBody();

            Date expiration = claims.getExpiration();

            //만료되면 true
            return expiration.before(new Date());
        }catch(ExpiredJwtException e){
            return true;
        }
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
    public ResponseCookie deleteRefreshToken(){
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
