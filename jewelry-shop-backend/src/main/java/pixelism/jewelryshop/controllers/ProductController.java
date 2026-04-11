package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.features.Product;
import pixelism.jewelryshop.features.User;
import pixelism.jewelryshop.repositories.ProductRepository;
import pixelism.jewelryshop.repositories.UserBehaviorRepository;
import pixelism.jewelryshop.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final UserBehaviorRepository userBehaviorRepository;
    private final UserRepository userRepository;
    private final Product product = new Product();

    @GetMapping
    public Page<Product> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable) {
        return product.getProducts(keyword, categoryId, minPrice, maxPrice, pageable, productRepository);
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return product.findProduct(id, productRepository);
    }

    @PostMapping
    public Product create(@RequestBody Product p) {
        return product.create(p, productRepository);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product p) {
        return product.update(id, p, productRepository);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        product.delete(id, productRepository);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<Product>> recommendations(@RequestParam(required = false) Long userId) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        return ResponseEntity.ok(product.getRecommendations(user, productRepository, userBehaviorRepository));
    }
}