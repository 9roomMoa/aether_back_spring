package com.groommoa.aether_back_spring.global.auth.controller;

import com.groommoa.aether_back_spring.global.auth.security.TokenProvider;
import com.groommoa.aether_back_spring.global.auth.service.TokenService;
import com.groommoa.aether_back_spring.global.common.constants.TokenKey;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @MockitoBean
    TokenService tokenService;

    @MockitoBean
    TokenProvider tokenProvider;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("인증 - 유효한 accessToken으로 인증 성공")
    void shouldAuthenticateWithValidAccessToken() throws Exception {
        // given: 유효한 accessToken
        String validAccessToken = "validAccessToken";

        // Mock 설정: accessToken이 유효한 경우
        when(tokenProvider.validateToken(validAccessToken)).thenReturn(true);

        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken("user", "", Collections.emptyList());
        when(tokenProvider.getAuthentication(validAccessToken)).thenReturn(mockAuthentication);

        // when: 유효한 토큰을 Authorization 헤더에 넣고 요청
        ResultActions result = mockMvc.perform(get("/auth/test")
                .header(AUTHORIZATION, TokenKey.TOKEN_PREFIX + validAccessToken));

        // then: 인증 성공하여 200 OK 응답을 받음
        result.andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("로그아웃 처리 - refresh 토큰 삭제")
    void logout() throws Exception {
        doNothing().when(tokenService).deleteRefreshToken(any());

        mockMvc.perform(delete("/auth/logout"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("accessToken 갱신 - 새로운 accessToken 반환")
    void reissueAccessToken() throws Exception {
        // given: 만료된 accessToken과 새로 재발급될 accessToken 설정
        String expiredAccessToken = "expiredAccessToken";
        String newAccessToken = "newAccessToken";

        // Mock 설정: 만료된 토큰은 유효하지 않음
        when(tokenProvider.validateToken(expiredAccessToken)).thenReturn(false);
        when(tokenProvider.reissueAccessToken(expiredAccessToken)).thenReturn(newAccessToken);
        when(tokenProvider.getAuthentication(newAccessToken))
                .thenReturn(new UsernamePasswordAuthenticationToken("user", "", Collections.emptyList())); // ✅ 인증 객체 Mocking

        ResultActions result = mockMvc.perform(get("/auth/reissue")
                .header(AUTHORIZATION, TokenKey.TOKEN_PREFIX + expiredAccessToken));

        result.andExpect(status().isOk())
                .andDo(print());
    }
}
