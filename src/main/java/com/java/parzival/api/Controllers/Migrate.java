package com.java.parzival.api.Controllers;

import com.java.parzival.Enums.UserRole;
import com.java.parzival.Model.Users;
import com.java.parzival.Repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class Migrate {
    private final UserRepository userRepository;

    @GetMapping("/migrate")
    public String Migration() {
        userRepository.save(
                Users.builder()
                        .username("admin")
                        .password(new BCryptPasswordEncoder().encode("admin"))
                        .role(UserRole.ADMIN)
                        .email("admin@admin.com")
                        .build()
        );
        return "success";
    }
}
