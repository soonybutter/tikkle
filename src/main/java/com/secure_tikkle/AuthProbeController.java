package com.secure_tikkle;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthProbeController {

	@GetMapping("/auth/success")
	  public String success() { return "Login OK — now call GET /me"; }

	  @GetMapping("/auth/failure")
	  public String failure() { return "Login Failed"; }

	  @GetMapping("/me")
	  public Map<String,Object> me(@AuthenticationPrincipal OAuth2User user) {
	    return Map.of("auth", user != null, "attributes", user != null ? user.getAttributes() : null);
	  }
}
