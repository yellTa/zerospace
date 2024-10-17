package com.zerospace.zerospace.filter;

import com.zerospace.zerospace.service.utils.JWTTokenService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.zerospace.zerospace.Const.Const.ACCESS_TOKEN_NAME;


@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JWTTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("===============JWT filter start ===============================");
        String accessToken = "";
        String refreshToken = "";

        // 요청 URI 가져오기
        String requestURI = request.getRequestURI();
        log.info("JWT requestURI = {}", requestURI);
        // 특정 URI (login/oauth2/code/kakao)는 필터 검사를 제외
        if (requestURI.startsWith("/login/oauth2/code/kakao") ||
                requestURI.startsWith("/loginResult") ||
                requestURI.startsWith("/oauth2/authorization/kakao") ||
                requestURI.startsWith("/error") ||
                requestURI.startsWith("/logoutzero")) {
            // 이 URI에 대해 필터를 건너뛰고 다음 필터로 넘어가기
            log.info("JWT Token skip URI = {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            accessToken = jwtTokenService.getAccessToken(request);
            refreshToken = jwtTokenService.getRefreshToken(request);
            log.info("userId = {}", jwtTokenService.getUserIdFromToken(accessToken));
        } catch (Exception e) {
            response.getWriter().write("Invalid access");
            log.info(e.toString());
            return;
        }

        try {
            if (!accessToken.equals("")) {
                if (!jwtTokenService.isTokenValidate(accessToken)) {//accesss토큰이 유효하지 않을때
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    ResponseCookie responseCookie = jwtTokenService.deleteRefreshToken();
                    response.addHeader("Set-Cookie", responseCookie.toString());
                    response.getWriter().write("Invalid accessToken");
                    return;
                }

                if (jwtTokenService.isTokenExpired(accessToken)) {//토큰이 만료된 경우
                    if (refreshToken != null && jwtTokenService.isTokenValidate(refreshToken)) {//refreshtoken이 유요한경우
                        String userId = jwtTokenService.getUserIdFromToken(accessToken);
                        String createdAccessToken = jwtTokenService.createAcecssToken(userId);
                        response.setHeader(ACCESS_TOKEN_NAME, createdAccessToken);
                    } else {
                        //유효하지 않은경우
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        ResponseCookie responseCookie = jwtTokenService.deleteRefreshToken();
                        response.addHeader("Set-Cookie", responseCookie.toString());
                        response.getWriter().write("Invalid refreshToken");
                        return;
                    }
                }
                filterChain.doFilter(request, response);

            } else {//AccessToken이 없는 경우
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ResponseCookie responseCookie = jwtTokenService.deleteRefreshToken();
                response.addHeader("Set-Cookie", responseCookie.toString());
                response.getWriter().write("No accessToken");
            }
        } catch (Exception e) {
            log.info(e.toString());
        }
    }
}
