package pixelism.jewelryshop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pixelism.jewelryshop.entities.*;
import pixelism.jewelryshop.repositories.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final ClaudeClientService claudeClient;

    @Transactional
    public ChatMessage sendMessage(User user, String content) {
        if (content == null || content.isBlank())
            throw new RuntimeException("Vui lòng nhập câu hỏi hợp lệ");

        ChatSession session = sessionRepository
                .findTopByUserAndStatusOrderByCreatedAtDesc(user, ChatSession.SessionStatus.ACTIVE)
                .orElseGet(() -> sessionRepository.save(
                        ChatSession.builder().user(user).status(ChatSession.SessionStatus.ACTIVE).build()
                ));

        ChatMessage userMsg = messageRepository.save(ChatMessage.builder()
                .session(session)
                .senderType(ChatMessage.SenderType.USER)
                .content(content)
                .status(ChatMessage.MessageStatus.DELIVERED)
                .build());

        // Build conversation history cho Claude
        List<Map<String, String>> history = messageRepository
                .findBySessionOrderByCreatedAtAsc(session)
                .stream()
                .filter(m -> m.getId() < userMsg.getId())
                .map(m -> Map.of(
                        "role", m.getSenderType() == ChatMessage.SenderType.USER ? "user" : "assistant",
                        "content", m.getContent()
                ))
                .collect(Collectors.toList());

        String reply = claudeClient.chat(history, content);

        if ("Không biết".equals(reply)) {
            reply = "Cung cấp thêm thông tin";
        }

        return messageRepository.save(ChatMessage.builder()
                .session(session)
                .senderType(ChatMessage.SenderType.BOT)
                .content(reply)
                .status(ChatMessage.MessageStatus.DELIVERED)
                .build());
    }

    public List<ChatMessage> getHistory(User user) {
        return sessionRepository
                .findTopByUserAndStatusOrderByCreatedAtDesc(user, ChatSession.SessionStatus.ACTIVE)
                .map(messageRepository::findBySessionOrderByCreatedAtAsc)
                .orElse(List.of());
    }
}