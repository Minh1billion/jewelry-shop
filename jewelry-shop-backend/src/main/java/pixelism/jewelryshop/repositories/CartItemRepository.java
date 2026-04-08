package pixelism.jewelryshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pixelism.jewelryshop.entities.Cart;
import pixelism.jewelryshop.entities.CartItem;
import pixelism.jewelryshop.entities.Product;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    List<CartItem> findByCart(Cart cart);
}