package com.zerospace.zerospace.Const;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.Acceleration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Slf4j
public class Const {
//    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 20 * 60 * 1000; // 20분
public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 20 * 60 * 1000; // 1분
    public static final long REFRESH_TOKEN_VALIDITY_SECONDS = 200 * 24 * 60 * 60  * 1000; //200일
    public static String SECRET_KEY = "zerosapceOur256SpecialKeyShouldBeVerySecureAndMuchLongerToMeetRequirements!";
    public static String ACCESS_TOKEN_NAME = "Authorization";
    public static String REFRESH_TOKEN_NAME = "refreshToken";


    public static Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

}
