package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.features.Product;
import pixelism.jewelryshop.features.User;
import pixelism.jewelryshop.repositories.UserRepository;
import pixelism.jewelryshop.services.ProductService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;

    @GetMapping
    public Page<Product> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable
    ) {
        return productService.getAll(keyword, categoryId, minPrice, maxPrice, pageable);
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id,
                           @RequestParam(required = false) Long userId) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        return productService.getById(id, user);
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product,
                          @RequestParam(required = false) Long userId) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        return productService.update(id, product, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}