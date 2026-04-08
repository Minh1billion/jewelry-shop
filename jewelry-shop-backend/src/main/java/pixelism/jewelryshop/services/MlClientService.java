package pixelism.jewelryshop.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MlClientService {

    private final WebClient.Builder webClientBuilder;

    @Value("${ml.service.url:http://localhost:8000}")
    private String mlServiceUrl;

    @Value("${ml.service.timeout-seconds:3}")
    private int timeoutSeconds;

    @SuppressWarnings("unchecked")
    public List<Long> getContentBasedRecommendations(Long userId, List<Long> productIds) {
        try {
            Map<String, Object> result = webClientBuilder.build()
                    .post()
                    .uri(mlServiceUrl + "/recommend/content-based")
                    .bodyValue(Map.of("user_id", userId, "product_ids", productIds))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .onErrorResume(e -> {
                        log.error("ML service error: {}", e.getMessage());
                        return Mono.empty();
                    })
                    .block();

            if (result == null || !result.containsKey("recommended_ids")) {
                return Collections.emptyList();
            }
            return (List<Long>) result.get("recommended_ids");

        } catch (Exception e) {
            log.error("ML service unreachable: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}