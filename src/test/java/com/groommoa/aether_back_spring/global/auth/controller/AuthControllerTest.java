package com.groommoa.aether_back_spring.global.auth.controller;

import com.groommoa.aether_back_spring.global.auth.security.TokenProvider;
import com.groommoa.aether_back_spring.global.auth.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
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
                .build();
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("로그아웃 처리 - refresh 토큰 삭제")
    void logout() throws Exception {
        doNothing().when(tokenService).deleteRefreshToken(any());

        mockMvc.perform(delete("/auth/logout"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
