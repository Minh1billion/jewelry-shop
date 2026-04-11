package pixelism.jewelryshop.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pixelism.jewelryshop.features.User;
import pixelism.jewelryshop.features.UserBehavior;
import pixelism.jewelryshop.repositories.ProductRepository;
import pixelism.jewelryshop.repositories.UserBehaviorRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBehaviorService {

    private final UserBehaviorRepository userBehaviorRepository;
    private final ProductRepository productRepository;

    public void track(User user, Long productId, UserBehavior.BehaviorType type) {
        if (user == null) return;
        productRepository.findById(productId).ifPresent(product -> {
            UserBehavior behavior = UserBehavior.builder()
                    .user(user)
                    .product(product)
                    .behaviorType(type)
                    .build();
            userBehaviorRepository.save(behavior);
        });

        System.out.println(" ✅ [Behavior Saved] User ID: " + user.getUserId()
                + " | Product ID: " + productId
                + " | Action: " + type);
    }
}
