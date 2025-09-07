package com.secure_tikkle.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class NewsController {

	private final WebClient web;   // ✅ 한 번만 선언

    // ✅ WebClient.Builder를 주입 받아서 WebClient 생성
    public NewsController(WebClient.Builder builder) {
        this.web = builder.build();
    }

    // 기본 RSS→JSON 변환 URL (원하면 yml로 빼세요)
    @Value("${news.default-url:https://feed2json.org/convert?url=https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko}")
    private String defaultUrl;

    @GetMapping(value = "/api/news", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getNews(@RequestParam(value = "url", required = false) String url) {
        String target = (url != null && !url.isBlank()) ? url : defaultUrl;

        return web.get()
                  .uri(target)
                  .retrieve()
                  .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                  .block(Duration.ofSeconds(10));
    }
}
