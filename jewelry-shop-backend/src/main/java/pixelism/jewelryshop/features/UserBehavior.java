package pixelism.jewelryshop.features;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import pixelism.jewelryshop.repositories.ProductRepository;
import pixelism.jewelryshop.repositories.UserBehaviorRepository;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_behaviors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long behaviorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BehaviorType behaviorType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum BehaviorType {
        VIEW, ADD_TO_CART, PURCHASE
    }

    private List<Product> getDefaultProducts(ProductRepository productRepository) {
        return productRepository.findNewestProducts(PageRequest.of(0, 10));
    }

    public List<Product> getRecommendations(User user,
                                            UserBehaviorRepository userBehaviorRepository,
                                            ProductRepository productRepository) {
        if (user == null || !userBehaviorRepository.existsByUser(user))
            return getDefaultProducts(productRepository);

        List<Long> interactedIds = userBehaviorRepository
                .findRecentProductsByUser(user)
                .stream()
                .map(Product::getProductId)
                .distinct()
                .toList();

        if (interactedIds.isEmpty())
            return getDefaultProducts(productRepository);

        List<Product> recommended = productRepository.findActiveByIds(interactedIds);
        return recommended.isEmpty() ? getDefaultProducts(productRepository) : recommended;
    }
}