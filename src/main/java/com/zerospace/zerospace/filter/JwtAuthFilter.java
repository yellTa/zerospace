package com.zerospace.zerospace.filter;

import com.zerospace.zerospace.service.utils.JWTTokenService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.zerospace.zerospace.Const.Const.ACCESS_TOKEN_NAME;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JWTTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("=============== JWT filter start ===============================");
        String requestURI = request.getRequestURI();
        log.info("JWT requestURI = {}", requestURI);

        // 특정 URI는 필터 검사를 제외
        if (shouldSkipFilter(requestURI)) {
            log.info("JWT Token skip URI = {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = jwtTokenService.getAccessToken(request);
            String refreshToken = jwtTokenService.getRefreshToken(request);

            if (accessToken == null || accessToken.isEmpty()) {
                handleUnauthorized(response, "No accessToken");
                return;
            }

//            if (!jwtTokenService.isTokenValidate(accessToken)) {
//                handleUnauthorized(response, "Invalid accessToken");
//                return;
//            }
            log.info("jwtTokenSErvice istokenExpired check start");
            if (jwtTokenService.isTokenExpired(accessToken)) {
                handleExpiredAccessToken(response, refreshToken, accessToken);
                filterChain.doFilter(request, response);
                return;
            }

            // 유효한 accessToken인 경우 다음 필터로 이동
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.warn("Error during JWT processing: {}", e);
            handleServerError(response, "Invalid access");
        }
    }

    private boolean shouldSkipFilter(String requestURI) {
        return requestURI.startsWith("/login/oauth2/code/kakao") ||
                requestURI.startsWith("/loginResult") ||
                requestURI.startsWith("/oauth2/authorization/kakao") ||
                requestURI.startsWith("/error") ||
                requestURI.startsWith("/logoutzero") ||
                requestURI.startsWith("/data");
    }

    private void handleExpiredAccessToken(HttpServletResponse response, String refreshToken, String accessToken) throws IOException {
        log.info("check refreshToken time");
        if (refreshToken != null && jwtTokenService.isTokenValidate(refreshToken)) {
            String userId = jwtTokenService.getUserIdFromToken(accessToken);
            String newAccessToken = jwtTokenService.createAcecssToken(userId);
            response.setHeader(ACCESS_TOKEN_NAME, newAccessToken);
            log.info("New accessToken issued for userId: {}", userId);
        } else {
            handleUnauthorized(response, "Invalid refreshToken");
            response.sendRedirect("https://zzerospace.store:8443/login/oauth2/code/kakao");
        }
    }

    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        log.info(message);
        ResponseCookie responseCookie = jwtTokenService.deleteRefreshToken();
        response.addHeader("Set-Cookie", responseCookie.toString());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(message);
    }

    private void handleServerError(HttpServletResponse response, String message) throws IOException {
        log.error(message);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write(message);
    }
}
