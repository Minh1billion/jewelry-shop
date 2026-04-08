package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.entities.User;
import pixelism.jewelryshop.services.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        User user = authService.register(
                body.get("username"),
                body.get("email"),
                body.get("password"),
                body.get("fullName"),
                body.get("phone")
        );
        return ResponseEntity.ok(Map.of("id", user.getId(), "username", user.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        User user = authService.login(body.get("username"), body.get("password"));
        return ResponseEntity.ok(Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole()));
    }
}