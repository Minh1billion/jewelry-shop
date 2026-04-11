package pixelism.jewelryshop.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pixelism.jewelryshop.features.Product;
import pixelism.jewelryshop.features.User;
import pixelism.jewelryshop.repositories.ProductRepository;
import pixelism.jewelryshop.repositories.UserBehaviorRepository;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserBehaviorRepository userBehaviorRepository;
    private final ProductRepository productRepository;

    private static final int DEFAULT_LIMIT = 10;

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    public List<Product> getRecommendations(User user) {
        if (user == null || !userBehaviorRepository.existsByUser(user)) {
            return getPopularProducts();
        }

        try {
            String fastApiUrl = mlServiceUrl + "/api/recommendations/" + user.getUserId();
            RestTemplate restTemplate = new RestTemplate();

            Map<String, List<Long>> response = restTemplate.exchange(
                    fastApiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, List<Long>>>() {}
            ).getBody();

            if (response != null && response.containsKey("recommended_ids")) {
                List<Long> recommendedIds = response.get("recommended_ids");
                if (!recommendedIds.isEmpty()) {
                    return productRepository.findActiveByIds(recommendedIds);
                }
            }

        } catch (Exception e) {
            log.error("FastAPI Error: {}", e.getMessage());
        }

        return getPopularProducts();
    }

    private List<Product> getPopularProducts() {
        List<Product> popular = productRepository.findTopSellingProducts(PageRequest.of(0, DEFAULT_LIMIT));
        if (!popular.isEmpty()) return popular;
        return productRepository.findNewestProducts(PageRequest.of(0, DEFAULT_LIMIT));
    }
}