package com.zerospace.zerospace;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@Slf4j
public class OAuthTest {

    @Autowired
    private MockMvc mock;

    @MockBean
    private OAuth2User oAuth2User;

    @Test
    public void TestOAuthSuccessHandler() throws Exception{
        Map<String, Object> userAttributes = Map.of(
                "kakao_account", Map.of("email", "test@example.com"),
                "properties", Map.of("nickname", "TestUser")
        );

        when(oAuth2User.getAttributes()).thenReturn(userAttributes);
        when(oAuth2User.getName()).thenReturn("testUser");

        mock.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andDo(result -> {
                    log.info("OAuthSuccessHandler 호출 완료.");
                });
    }

}
