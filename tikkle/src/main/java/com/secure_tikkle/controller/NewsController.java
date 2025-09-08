package com.secure_tikkle.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference; 
import org.springframework.http.CacheControl;              
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
public class NewsController {

	 private static final Logger log = LoggerFactory.getLogger(NewsController.class);

	    private final WebClient web;

	    public NewsController(WebClient.Builder builder) {
	        this.web = builder.baseUrl("https://newsapi.org").build();
	    }

	    @Value("${news.api.key:}")
	    private String apiKey;

	    @GetMapping("/api/news")
	    public Mono<ResponseEntity<Map<String, Object>>> getNews() {
	        if (apiKey == null || apiKey.isBlank()) {
	            log.warn("[NEWS] apiKey missing – returning empty list");
	            return Mono.just(ResponseEntity.ok(Map.of("articles", List.of())));
	        }

	        return callTopHeadlines()
	            .flatMap(body -> {
	                if (hasArticles(body)) return Mono.just(body);
	                log.info("[NEWS] top-headlines empty or invalid → fallback to everything");
	                return callEverything();
	            })
	            .map(body -> ResponseEntity.ok()
	                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
	                .body(body))
	            .onErrorResume(e -> {
	                log.warn("[NEWS] error: {}", e.toString());
	                return Mono.just(ResponseEntity.ok(Map.of("articles", List.of())));
	            });
	    }

	    private Mono<Map<String, Object>> callTopHeadlines() {
	        return web.get()
	            .uri(uri -> uri.path("/v2/top-headlines")
	                .queryParam("country", "kr")
	                .queryParam("category", "business")
	                .queryParam("pageSize", "12")
	                .build())
	            .header("X-Api-Key", apiKey)
	            .retrieve()
	            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
	            .doOnNext(b -> log.debug("[NEWS] top-headlines status={}, totalResults={}",
	                b.get("status"), b.get("totalResults")))
	            .doOnError(err -> log.warn("[NEWS] top-headlines error: {}", err.toString()));
	    }

	    private Mono<Map<String, Object>> callEverything() {
	        // 한국어 경제 관련 키워드들로 최근순
	        return web.get()
	            .uri(uri -> uri.path("/v2/everything")
	                .queryParam("q", "(경제 OR 증시 OR 금리 OR 물가 OR 코스피)")
	                .queryParam("language", "ko")
	                .queryParam("sortBy", "publishedAt")
	                .queryParam("pageSize", "12")
	                .build())
	            .header("X-Api-Key", apiKey)
	            .retrieve()
	            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
	            .doOnNext(b -> log.debug("[NEWS] everything status={}, totalResults={}",
	                b.get("status"), b.get("totalResults")))
	            .doOnError(err -> log.warn("[NEWS] everything error: {}", err.toString()));
	    }

	    private boolean hasArticles(Map<String, Object> body) {
	        if (!"ok".equals(body.get("status"))) return false;
	        Object arts = body.get("articles");
	        return (arts instanceof List) && !((List<?>) arts).isEmpty();
	    }
}