package pixelism.jewelryshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pixelism.jewelryshop.entities.ChatMessage;
import pixelism.jewelryshop.entities.ChatSession;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySessionOrderByCreatedAtAsc(ChatSession session);
}