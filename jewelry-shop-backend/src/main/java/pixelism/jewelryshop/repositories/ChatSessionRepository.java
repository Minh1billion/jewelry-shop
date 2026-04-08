package pixelism.jewelryshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pixelism.jewelryshop.entities.ChatSession;
import pixelism.jewelryshop.entities.User;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findTopByUserAndStatusOrderByCreatedAtDesc(
            User user, ChatSession.SessionStatus status);
}