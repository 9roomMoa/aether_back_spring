package com.groommoa.aether_back_spring.global.config;

import com.groommoa.aether_back_spring.global.auth.handler.CustomAccessDeniedHandler;
import com.groommoa.aether_back_spring.global.auth.handler.CustomAuthenticationEntryPoint;
import com.groommoa.aether_back_spring.global.auth.handler.OAuth2SuccessHandler;
import com.groommoa.aether_back_spring.global.auth.security.TokenAuthenticationFilter;
import com.groommoa.aether_back_spring.global.auth.security.TokenExceptionFilter;
import com.groommoa.aether_back_spring.global.auth.service.CustomOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

/**
 * Spring Security의 보안 설정을 담당하는 Spring Configuration
 * <p></p>
 * - JWT 기반 인증
 * - OAuth2 로그인
 * - CSRF 비활성화
 * - 세션 관리 (Stateless)
 * - 인증 및 인가 정책 설정
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // @PreAuthorize, @Secured 등의 어노테이션 기반 보안 활성화
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    /**
     * 특정 요청을 Security 필터링에서 제외
     * @return WebSecurityCustomizer 객체
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/error", "/favicon.ico"); // 에러 페이지 및 파비콘 요청 제외
    }

    /**
     * CORS 설정을 위한 CorsConfigurationSource를 정의
     * <P></P>
     * CORS 요청에 대해 허용할 설정 지정
     */
    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Collections.singletonList("http://localhost:5173"));
            config.setAllowCredentials(true);
            return config;
        };
    }

    /**
     * Spring Security의 보안 설정을 정의하는 SecurityFilterChain 생성
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 객체
     * @throws Exception 설정 중 예외 발생 시 처리
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (JWT 사용 시 필요)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정 적용
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))

                // HTTP 기본 인증 비활성화 (JWT 사용)
                .httpBasic(AbstractHttpConfigurer::disable)

                // Form 로그인 비활성화 (OAuth2 사용)
                .formLogin(AbstractHttpConfigurer::disable)

                // X-Frame-Options 비활성화 (H2 콘솔 사용 시 필요)
                .headers((headerConfig) ->
                        headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable
                        )
                )

                // 세션 관리 - STATELESS (JWT 기반 인증을 사용하기 때문)
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 인증 및 인가 설정
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                new AntPathRequestMatcher("/h2-console/**"),    // H2 콘솔 접근 허용
                                new AntPathRequestMatcher("/"),                 // 루트 경로 접근 허용
                                new AntPathRequestMatcher("/auth/success")      // OAuth2 로그인 성공 시 접근 허용
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 로그아웃 설정
                .logout(
                        (logoutConfig) -> logoutConfig.logoutSuccessUrl("/")
                )

                // OAuth2 로그인 설정
                .oauth2Login(auth -> auth
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint

                                // OAuth2 사용자 정보 서비스 설정
                                .userService(customOauth2UserService))

                        // OAuth2 로그인 성공 시 실행할 핸들러
                        .successHandler(oAuth2SuccessHandler)
                )
                // JWT 관련 필터 추가
                .addFilterBefore(tokenAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass())

                // 인증 및 권한 예외 처리 설정
                .exceptionHandling(e -> e

                        // 인증 실패 처리
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())

                        // 인가 실패 처리
                        .accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }
}