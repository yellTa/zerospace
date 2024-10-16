package com.zerospace.zerospace.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CORSFilter implements Filter {

    private static final List<String> allowedOrigins = Arrays.asList(
            //여기에 버셀주소 적으면 되는거임
            "https://localhost:3000",
            "http://localhost:3000",
            "https://zero-space-service.vercel.app",
            "https://zero-space-service-snowy.vercel.app"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/oauth2/authorization/kakao") || requestURI.startsWith("/login/oauth2/code/kakao") || requestURI.startsWith("/error")) {
            log.info("==========CORS SKIP================");
            chain.doFilter(req, res); // 필터를 넘기고 바로 리턴
            return;
        }
        log.info("CORS fILTER START =============================================");
        log.info(request.getRequestURI());
        String userAgent = request.getHeader("User-Agent");
        log.info("User-Agent : {}", userAgent);

        String origin = request.getHeader("Origin");
        log.info("================add CORS Header==================");
        if (allowedOrigins.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me, Authorization, cache-control");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("CORS END ==================================");
            return;
        }
        log.info("CORS dp filter END=======================================");
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
