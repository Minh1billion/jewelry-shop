package pixelism.jewelryshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pixelism.jewelryshop.entities.Cart;
import pixelism.jewelryshop.entities.User;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}