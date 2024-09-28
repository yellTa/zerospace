package com.zerospace.zerospace.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.Date;

import static com.zerospace.zerospace.Const.Const.*;


@Component
public class JWTTokenService {
    public boolean validateToken(String token){



        return true;
    }

    public String createAcecssToken(String userId){
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


    public String createRefreshToken(String userId){
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

}
