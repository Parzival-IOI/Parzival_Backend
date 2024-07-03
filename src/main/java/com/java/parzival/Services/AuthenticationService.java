package com.java.parzival.Services;

import com.java.parzival.DataTransferObjects.AuthenticationDTO.LoginRequest;
import com.java.parzival.DataTransferObjects.AuthenticationDTO.TokenResponse;
import com.java.parzival.Model.BlockedUsers;
import com.java.parzival.Model.Logins;
import com.java.parzival.Model.Users;
import com.java.parzival.Repository.BlockedUserRepository;
import com.java.parzival.Repository.LoginRepository;
import com.java.parzival.Repository.UserRepository;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final BlockedUserRepository blockedUserRepository;
    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;
    private final LoginRepository loginRepository;

    public TokenResponse login(LoginRequest loginRequest) {

        log.info(loginRequest.username());
        Optional<Users> user = userRepository.findByUsername(loginRequest.username());

        if(user.isPresent()) {
            if(!user.get().getPassword().equals(loginRequest.password())) {
                Optional<BlockedUsers> blockedUserModel = blockedUserRepository.findByUserId(user.get().getId());
                if(blockedUserModel.isPresent()) {
                    int attempts = blockedUserModel.get().getAttempt();
                    if(attempts > 5) {
                        throw new RuntimeException("Attempt limit exceeded");
                    } else {
                        blockedUserModel.get().setAttempt(attempts + 1);
                        blockedUserRepository.save(blockedUserModel.get());
                    }
                } else {
                    blockedUserRepository.insert(
                            BlockedUsers.builder()
                                    .userId(user.get().getId())
                                    .attempt(1)
                                    .build()
                    );
                }
            }
            log.info("testing");
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
            Instant now = Instant.now();
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));
            //access token
            JwtClaimsSet accessToken = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(15*60))
                    .subject(authentication.getName())
                    .claim("role", role)
                    .build();

            //refresh token
            JwtClaimsSet refreshToken = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plus(1, ChronoUnit.HOURS))
                    .subject(authentication.getName())
                    .claim("role", "ROLE_REFRESH_TOKEN")
                    .claim("token", "refresh")
                    .build();

            String generatedAccessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(accessToken)).getTokenValue();
            String generatedRefreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(refreshToken)).getTokenValue();

            String createdToken = "Login : " + authentication.getName() + "/" + role + "/" + generatedAccessToken;
            log.info(createdToken);

            Optional<Logins> login = loginRepository.findByUserId(user.get().getId());
            if(login.isPresent()) {
                login.get().setRefreshToken(generatedRefreshToken);
                loginRepository.save(login.get());
            } else {
                loginRepository.insert(
                        Logins.builder()
                                .userId(user.get().getId())
                                .refreshToken(generatedRefreshToken)
                                .build()
                );
            }

            return TokenResponse.builder()
                    .accessToken(generatedAccessToken)
                    .refreshToken(generatedRefreshToken)
                    .build();
        }

        throw new RuntimeException("User Not Found");
    }

    public TokenResponse refreshToken(Principal principal, Jwt jwt) {
        Optional<Users> user = userRepository.findByUsername(principal.getName());
        if(user.isPresent()) {
            Optional<Logins> login = loginRepository.findByUserId(user.get().getId());
            if(login.isPresent()) {
                if(!login.get().getRefreshToken().equals(jwt.getTokenValue())) {
                    throw new RuntimeException("Refresh Token is not valid");
                }
            }
            else {
                throw new RuntimeException("Invalid Refresh Token");
            }

            Instant now = Instant.now();
            String role = user.get().getRole().getValue();
            //access token
            JwtClaimsSet accessToken = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(15*60))
                    .subject(user.get().getUsername())
                    .claim("role", "ROLE_" + role)
                    .build();

            //refresh token
            JwtClaimsSet refreshToken = JwtClaimsSet.builder()
                    .issuer("self")
                    .issuedAt(now)
                    .expiresAt(now.plus(1, ChronoUnit.HOURS))
                    .subject(user.get().getUsername())
                    .claim("role", "ROLE_REFRESH_TOKEN")
                    .claim("token", "refresh")
                    .build();

            String generatedAccessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(accessToken)).getTokenValue();
            String generatedRefreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(refreshToken)).getTokenValue();

            login.get().setRefreshToken(generatedRefreshToken);
            loginRepository.save(login.get());

            String createdToken = "refresh : " + user.get().getUsername() + "/" + role + "/" + generatedAccessToken;
            log.info(createdToken);

            return TokenResponse.builder()
                    .accessToken(generatedAccessToken)
                    .refreshToken(generatedRefreshToken)
                    .build();
        }
        throw new RuntimeException("User Not Found");
    }
}
