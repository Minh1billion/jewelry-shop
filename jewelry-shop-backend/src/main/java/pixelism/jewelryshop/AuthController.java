package pixelism.jewelryshop;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.repositories.CartRepository;
import pixelism.jewelryshop.repositories.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final User user = new User();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        User registered = user.register(
                body.get("username"),
                body.get("email"),
                body.get("password"),
                body.get("fullName"),
                body.get("phone"),
                userRepository, cartRepository
        );
        return ResponseEntity.ok(Map.of("id", registered.getUserId(), "username", registered.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        User logged = user.login(body.get("username"), body.get("password"), userRepository);
        return ResponseEntity.ok(Map.of("id", logged.getUserId(), "username", logged.getUsername(), "role", logged.getRole()));
    }
}