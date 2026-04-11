package pixelism.jewelryshop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import pixelism.jewelryshop.repositories.ChatMessageRepository;
import pixelism.jewelryshop.repositories.ChatSessionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ChatMessage> messages;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = SessionStatus.ACTIVE;
    }

    public enum SessionStatus {
        ACTIVE, CLOSED
    }

    public List<ChatMessage> getHistory(User user,
                                        ChatSessionRepository sessionRepository,
                                        ChatMessageRepository messageRepository) {
        return sessionRepository
                .findTopByUserAndStatusOrderByCreatedAtDesc(user, SessionStatus.ACTIVE)
                .map(messageRepository::findBySessionOrderByCreatedAtAsc)
                .orElse(List.of());
    }
}