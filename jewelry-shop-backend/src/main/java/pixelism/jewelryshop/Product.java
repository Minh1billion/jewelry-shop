package pixelism.jewelryshop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pixelism.jewelryshop.repositories.ProductRepository;
import pixelism.jewelryshop.repositories.UserBehaviorRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<UserBehavior> behaviors;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    private static final int DEFAULT_LIMIT = 10;

    public Page<Product> getProducts(String keyword, Long categoryId,
                                     BigDecimal minPrice, BigDecimal maxPrice,
                                     Pageable pageable, ProductRepository productRepository) {
        return productRepository.findWithFilters(keyword, categoryId, minPrice, maxPrice, pageable);
    }

    public Product findProduct(Long id, ProductRepository productRepository) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    public Product create(Product product, ProductRepository productRepository) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product updated, ProductRepository productRepository) {
        Product existing = findProduct(id, productRepository);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory());
        existing.setActive(updated.getActive());
        return productRepository.save(existing);
    }

    public void delete(Long id, ProductRepository productRepository) {
        productRepository.deleteById(id);
    }

    public List<Product> getRecommendations(User user,
                                            ProductRepository productRepository,
                                            UserBehaviorRepository userBehaviorRepository) {
        if (user == null || !userBehaviorRepository.existsByUser(user)) {
            log.info("New user or guest — returning popular products");
            return getPopularProducts(productRepository);
        }

        List<Long> interactedIds = userBehaviorRepository
                .findRecentProductsByUser(user)
                .stream()
                .map(Product::getProductId)
                .distinct()
                .toList();

        if (interactedIds.isEmpty())
            return getPopularProducts(productRepository);

        List<Product> recommended = productRepository.findActiveByIds(interactedIds);
        return recommended.isEmpty() ? getPopularProducts(productRepository) : recommended;
    }

    private List<Product> getPopularProducts(ProductRepository productRepository) {
        List<Product> popular = productRepository.findTopSellingProducts(PageRequest.of(0, DEFAULT_LIMIT));
        if (!popular.isEmpty()) return popular;
        return productRepository.findNewestProducts(PageRequest.of(0, DEFAULT_LIMIT));
    }
}