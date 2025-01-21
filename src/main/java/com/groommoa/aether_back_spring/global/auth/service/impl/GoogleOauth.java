package com.groommoa.aether_back_spring.global.auth.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.groommoa.aether_back_spring.global.auth.service.SocialOauth;
import com.groommoa.aether_back_spring.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// 공통 interface를 구현할 소셜 로그인 각 타입별 Class 생성 (Google)
@Component
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${sns.google.url}")
    private String GOOGLE_SNS_BASE_URL;
    @Value("${sns.google.client.id}")
    private String GOOGLE_SNS_CLIENT_ID;
    @Value("${sns.google.client.secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;
    @Value("${sns.google.redirect.url}")
    private String GOOGLE_SNS_REDIRECT_URL;
    @Value("${sns.google.token.url}")
    private String GOOGLE_SNS_TOKEN_BASE_URL;
    @Value("${sns.google.resource.url}")
    private String GOOGLE_SNS_RESOURCE_URL;

    @Override
    public String getOauthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("scope", "profile email");
        params.put("response_type", "code");
        params.put("client_id", GOOGLE_SNS_CLIENT_ID);
        params.put("redirect_uri", GOOGLE_SNS_REDIRECT_URL);

        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        return GOOGLE_SNS_BASE_URL + "?" + parameterString;
    }

    @Override
    public ResponseEntity<BaseResponse> getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", GOOGLE_SNS_CLIENT_ID);
        params.add("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        params.add("redirect_uri", GOOGLE_SNS_REDIRECT_URL);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(
                GOOGLE_SNS_TOKEN_BASE_URL, HttpMethod.POST, entity, JsonNode.class);
        JsonNode accessTokenNode = responseNode.getBody();


        BaseResponse responseBody;
        if (accessTokenNode == null) {
            responseBody = new BaseResponse(400, "Google 로그인에 실패했습니다.", null);
            return ResponseEntity.badRequest().body(responseBody);
        }
        String accessToken = accessTokenNode.get("access_token").asText();

        Map<String, Object> result = new HashMap<>();
        result.put("access_token", accessToken);

        responseBody = new BaseResponse(200, "Google 로그인에 성공했습니다.", result);
        return ResponseEntity.ok(responseBody);
    }

    @Override
    public ResponseEntity<BaseResponse> getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        try {
            // Google API 호출
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    GOOGLE_SNS_RESOURCE_URL,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );

            JsonNode userInfo = response.getBody();

            // 결과가 null인지 확인
            if (userInfo == null) {
                return ResponseEntity.badRequest()
                        .body(new BaseResponse(400, "Google 사용자 정보를 가져올 수 없습니다.", null));
            }

            Map<String, Object> result = new HashMap<>();
            result.put("id", userInfo.get("id").asText());
            result.put("email", userInfo.get("email").asText());
            result.put("name", userInfo.get("name").asText());
            result.put("picture", userInfo.get("picture").asText());

            // 성공적인 응답
            BaseResponse responseBody = new BaseResponse(200, "Google 계정 프로필 정보를 성공적으로 가져왔습니다.", result);
            return ResponseEntity.ok(responseBody);

        } catch (HttpClientErrorException e) {
            // 클라이언트 오류 처리 (예: 401 Unauthorized)
            BaseResponse responseBody = new BaseResponse(e.getStatusCode().value(), "Google API 요청에 실패했습니다.", null);
            return ResponseEntity.status(e.getStatusCode()).body(responseBody);

        } catch (Exception e) {
            // 기타 예외 처리
            BaseResponse responseBody = new BaseResponse(500, "알 수 없는 오류가 발생했습니다.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

}