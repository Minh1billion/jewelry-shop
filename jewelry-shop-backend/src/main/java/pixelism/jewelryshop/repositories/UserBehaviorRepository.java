package pixelism.jewelryshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pixelism.jewelryshop.entities.Product;
import pixelism.jewelryshop.entities.User;
import pixelism.jewelryshop.entities.UserBehavior;
import java.util.List;

public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {
    List<UserBehavior> findByUserOrderByCreatedAtDesc(User user);
    boolean existsByUser(User user);

    @Query("SELECT b.product FROM UserBehavior b WHERE b.user = :user ORDER BY b.createdAt DESC")
    List<Product> findRecentProductsByUser(User user);
}