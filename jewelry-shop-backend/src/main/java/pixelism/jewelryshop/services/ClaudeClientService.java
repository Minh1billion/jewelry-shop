package pixelism.jewelryshop.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaudeClientService {

    private final WebClient.Builder webClientBuilder;

    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.timeout-seconds:10}")
    private int timeoutSeconds;

    private static final String CLAUDE_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-sonnet-4-20250514";
    private static final String SYSTEM_PROMPT = """
        Bạn là trợ lý tư vấn của cửa hàng trang sức Pixelism.
        Hãy tư vấn về sản phẩm, đơn hàng và chính sách cửa hàng.
        Nếu không hiểu câu hỏi, hãy yêu cầu người dùng cung cấp thêm thông tin.
        Trả lời ngắn gọn, thân thiện bằng tiếng Việt.
        Nếu không đủ thông tin hoặc câu hỏi không liên quan hãy trả lời chính xác là "Không biết".
        """;

    @SuppressWarnings("unchecked")
    public String chat(List<Map<String, String>> history, String userMessage) {
        List<Map<String, String>> messages = new java.util.ArrayList<>(history);
        messages.add(Map.of("role", "user", "content", userMessage));

        try {
            Map<String, Object> body = Map.of(
                    "model", MODEL,
                    "max_tokens", 1024,
                    "system", SYSTEM_PROMPT,
                    "messages", messages
            );

            Map result = webClientBuilder.build()
                    .post()
                    .uri(CLAUDE_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .onErrorResume(e -> {
                        log.error("Claude API error: {}", e.getMessage());
                        return Mono.empty();
                    })
                    .block();

            if (result == null) return null;

            List<Map<String, Object>> content = (List<Map<String, Object>>) result.get("content");
            if (content == null || content.isEmpty()) return null;
            return (String) content.get(0).get("text");

        } catch (Exception e) {
            log.error("Claude client exception: {}", e.getMessage());
            return null;
        }
    }
}