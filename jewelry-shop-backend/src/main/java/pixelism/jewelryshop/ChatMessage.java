package pixelism.jewelryshop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import pixelism.jewelryshop.repositories.ChatMessageRepository;
import pixelism.jewelryshop.repositories.ChatSessionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnore
    private ChatSession session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderType senderType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = MessageStatus.DELIVERED;
    }

    public enum SenderType {
        USER, BOT, ADMIN
    }

    public enum MessageStatus {
        PENDING, DELIVERED, FAILED
    }

    public ChatMessage sendMessage(User user, String content,
                                   ChatSessionRepository sessionRepository,
                                   ChatMessageRepository messageRepository,
                                   ChatService chatService) {
        if (content == null || content.isBlank())
            throw new RuntimeException("Vui lòng nhập câu hỏi hợp lệ");

        ChatSession session = sessionRepository
                .findTopByUserAndStatusOrderByCreatedAtDesc(user, ChatSession.SessionStatus.ACTIVE)
                .orElseGet(() -> sessionRepository.save(
                        ChatSession.builder().user(user).status(ChatSession.SessionStatus.ACTIVE).build()
                ));

        messageRepository.save(ChatMessage.builder()
                .session(session)
                .senderType(SenderType.USER)
                .content(content)
                .status(MessageStatus.DELIVERED)
                .build());

        String reply = chatService.chat(new ArrayList<>(), content);

        if (reply == null || reply.isBlank() || "Không biết".equals(reply))
            reply = "Xin lỗi, tôi chưa có đủ thông tin để trả lời. Bạn có thể cung cấp thêm chi tiết không?";

        return messageRepository.save(ChatMessage.builder()
                .session(session)
                .senderType(SenderType.BOT)
                .content(reply)
                .status(MessageStatus.DELIVERED)
                .build());
    }

    public List<ChatMessage> getHistory(User user,
                                        ChatSessionRepository sessionRepository,
                                        ChatMessageRepository messageRepository) {
        return sessionRepository
                .findTopByUserAndStatusOrderByCreatedAtDesc(user, ChatSession.SessionStatus.ACTIVE)
                .map(messageRepository::findBySessionOrderByCreatedAtAsc)
                .orElse(List.of());
    }
}