package pixelism.jewelryshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pixelism.jewelryshop.features.Product;
import pixelism.jewelryshop.features.User;
import pixelism.jewelryshop.features.UserBehavior;

import java.util.List;

public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {
    List<UserBehavior> findByUserOrderByCreatedAtDesc(User user);
    boolean existsByUser(User user);

    @Query("SELECT b.product FROM UserBehavior b WHERE b.user = :user ORDER BY b.createdAt DESC")
    List<Product> findRecentProductsByUser(User user);
}