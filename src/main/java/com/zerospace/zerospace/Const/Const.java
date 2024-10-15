package com.zerospace.zerospace.Const;

import org.springframework.beans.factory.annotation.Value;

public class Const {
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 20 * 60; // 20분
    public static final long REFRESH_TOKEN_VALIDITY_SECONDS = 30 * 24 * 60 * 60; //30일

    @Value("${jwt.secret-key}")
    private String secretKeyValue;

    @Value("${jwt.access-token-name}")
    private String accessTokenNameValue;

    @Value("${jwt.refresh-token-name}")
    private String refreshTokenNameValue;

    public static String SECRET_KEY;
    public static String ACCESS_TOKEN_NAME;
    public static String REFRESH_TOKEN_NAME;
}
