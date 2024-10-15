package com.zerospace.zerospace.Const;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Const {
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 20 * 60; // 20분
    public static final long REFRESH_TOKEN_VALIDITY_SECONDS = 30 * 24 * 60 * 60; //30일

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-name}")
    private String accessTokenName;

    @Value("${jwt.refresh-token-name}")
    private String refreshTokenName;

    public static String SECRET_KEY;

    public static String ACCESS_TOKEN_NAME;

    public static  String REFRESH_TOKEN_NAME;

    @PostConstruct
    public void init(){
        SECRET_KEY = secretKey;
        ACCESS_TOKEN_NAME = accessTokenName;
        REFRESH_TOKEN_NAME = refreshTokenName;
    }
}
