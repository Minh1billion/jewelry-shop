package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.entities.Product;
import pixelism.jewelryshop.entities.User;
import pixelism.jewelryshop.repositories.UserRepository;
import pixelism.jewelryshop.services.RecommendationService;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Product>> getRecommendations(
            @RequestParam(required = false) Long userId) {

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        return ResponseEntity.ok(recommendationService.getRecommendations(user));
    }
}