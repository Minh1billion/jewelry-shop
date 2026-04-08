package pixelism.jewelryshop.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pixelism.jewelryshop.entities.Product;
import pixelism.jewelryshop.entities.User;
import pixelism.jewelryshop.repositories.ProductRepository;
import pixelism.jewelryshop.repositories.UserBehaviorRepository;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserBehaviorRepository userBehaviorRepository;
    private final ProductRepository productRepository;
    private final MlClientService mlClientService;

    private static final int DEFAULT_LIMIT = 10;

    public List<Product> getRecommendations(User user) {
        if (user == null || !userBehaviorRepository.existsByUser(user)) {
            log.info("New user or guest — returning popular products");
            return getPopularProducts();
        }

        List<Long> interactedIds = userBehaviorRepository
                .findRecentProductsByUser(user)
                .stream()
                .map(Product::getId)
                .distinct()
                .toList();

        if (interactedIds.isEmpty()) {
            return getPopularProducts();
        }

        List<Long> recommendedIds = mlClientService
                .getContentBasedRecommendations(user.getId(), interactedIds);

        if (recommendedIds.isEmpty()) {
            log.warn("ML service returned empty — falling back to random products");
            return getPopularProducts();
        }

        List<Product> recommended = productRepository.findActiveByIds(recommendedIds);

        return recommended;
    }

    private List<Product> getPopularProducts() {
        List<Product> popular = productRepository.findTopSellingProducts(PageRequest.of(0, DEFAULT_LIMIT));
        if (!popular.isEmpty()) return popular;
        return productRepository.findNewestProducts(PageRequest.of(0, DEFAULT_LIMIT));
    }
}