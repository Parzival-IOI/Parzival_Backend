package com.java.parzival.api.Controllers;

import com.java.parzival.DataTransferObjects.AuthenticationDTO.LoginRequest;
import com.java.parzival.DataTransferObjects.AuthenticationDTO.TokenResponse;
import com.java.parzival.Services.AuthenticationService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authenticate/")
@RequiredArgsConstructor
public class Authentication {
    private final AuthenticationService authenticationService;

    @PostMapping("login")
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @PostMapping("refreshToken")
    public TokenResponse refreshToken(Principal principal, @AuthenticationPrincipal Jwt jwt) {
        return authenticationService.refreshToken(principal, jwt);
    }
}
