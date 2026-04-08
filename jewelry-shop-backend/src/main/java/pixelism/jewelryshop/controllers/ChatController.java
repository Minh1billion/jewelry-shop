package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.entities.*;
import pixelism.jewelryshop.repositories.UserRepository;
import pixelism.jewelryshop.services.ChatService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> send(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        String content = body.get("content");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(chatService.sendMessage(user, content));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> history(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(chatService.getHistory(user));
    }
}