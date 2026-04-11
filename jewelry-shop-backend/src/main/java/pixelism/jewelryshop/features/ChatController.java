package pixelism.jewelryshop.features;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.repositories.ChatMessageRepository;
import pixelism.jewelryshop.repositories.ChatSessionRepository;
import pixelism.jewelryshop.repositories.UserRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final ChatMessage chatMessage = new ChatMessage();

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> send(@RequestBody Map<String, String> body) {
        User user = userRepository.findById(Long.valueOf(body.get("userId")))
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(chatMessage.sendMessage(user, body.get("content"),
                sessionRepository, messageRepository, chatService));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> history(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(chatMessage.getHistory(user, sessionRepository, messageRepository));
    }
}