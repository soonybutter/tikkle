
package com.secure_tikkle.controller;

import org.springframework.security.oauth2.client.registration.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class DebugOAuthClientsController {
    private final ClientRegistrationRepository repo;
    public DebugOAuthClientsController(ClientRegistrationRepository repo) { this.repo = repo; }

    @GetMapping("/debug/oauth/clients")
    public List<String> clients() {
    	
        List<String> ids = new ArrayList<>();
        
        if (repo instanceof InMemoryClientRegistrationRepository mem) {
            mem.forEach(r -> ids.add(r.getRegistrationId()));
        }
        
        return ids;
    }
}
