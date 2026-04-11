package pixelism.jewelryshop.features;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChatService {

    private final WebClient.Builder webClientBuilder;

    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.timeout-seconds:10}")
    private int timeoutSeconds;

    public ChatService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    private static final String CLAUDE_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";
    private static final String SYSTEM_PROMPT = """
        Bạn là trợ lý tư vấn của cửa hàng trang sức Pixelism.
        Hãy tư vấn về sản phẩm, đơn hàng và chính sách cửa hàng.
        Nếu không hiểu câu hỏi, hãy yêu cầu người dùng cung cấp thêm thông tin.
        Trả lời ngắn gọn, thân thiện bằng tiếng Việt.
        Nếu không đủ thông tin hoặc câu hỏi không liên quan hãy trả lời chính xác là "Không biết".
        """;

    @SuppressWarnings("unchecked")
    public String chat(List<Map<String, String>> history, String userMessage) {
        log.info("Groq API key prefix: {}", apiKey != null ? apiKey.substring(0, 10) : "NULL");
        log.info("Calling Groq with message: {}", userMessage);
        log.info("History size: {}", history.size());

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", SYSTEM_PROMPT);
        messages.add(systemMsg);

        messages.addAll(history);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        log.info("Total messages sent to Groq: {}", messages.size());

        try {
            Map<String, Object> body = Map.of(
                    "model", MODEL,
                    "max_tokens", 1024,
                    "messages", messages
            );

            Map result = webClientBuilder.build()
                    .post()
                    .uri(CLAUDE_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .onErrorResume(e -> {
                        log.error("Groq API error: {}", e.getMessage());
                        if (e instanceof org.springframework.web.reactive.function.client.WebClientResponseException ex)
                            log.error("Groq error body: {}", ex.getResponseBodyAsString());
                        return Mono.empty();
                    })
                    .block();

            log.info("Groq result: {}", result);

            if (result == null) return "Không biết";

            List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
            if (choices == null || choices.isEmpty()) return "Không biết";

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String text = (String) message.get("content");
            return text != null ? text : "Không biết";

        } catch (Exception e) {
            log.error("ChatService exception: {}", e.getMessage());
            return "Không biết";
        }
    }
}