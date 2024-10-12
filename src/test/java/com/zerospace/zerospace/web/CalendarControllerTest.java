package com.zerospace.zerospace.web;

import com.zerospace.zerospace.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {TestSecurityConfig.class})  // 테스트 보안 설정 적용
class CalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testApiWithoutOAuth() throws Exception {
        mockMvc.perform(post("/calendar/platform")
                        .with(csrf()))  // CSRF 토큰 추가
                .andExpect(status().isOk());
    }

    @Test
    public void testPostWithFormData() throws Exception {
        mockMvc.perform(post("/calendar/platform")
                        .param("email", "abc@gmail.com")
                        .param("platform", "hourplace")
                        .param("password", "1123123")
                        .with(csrf()))  // 최신 CSRF 사용법
                .andExpect(status().isOk());  // 상태 코드 검증
    }


}