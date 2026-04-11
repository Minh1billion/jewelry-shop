package pixelism.jewelryshop.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pixelism.jewelryshop.features.Product;
import pixelism.jewelryshop.features.User;
import pixelism.jewelryshop.features.UserBehavior;
import pixelism.jewelryshop.repositories.ProductRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserBehaviorService userBehaviorService;

    public Page<Product> getAll(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findWithFilters(keyword, categoryId, minPrice, maxPrice, pageable);
    }

    // Trong method getProductById hoặc controller
    public Product getById(Long id, User user) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        userBehaviorService.track(user, id, UserBehavior.BehaviorType.VIEW);
        return product;
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product updated, User user) {
        Product existing = getById(id, user);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory());
        existing.setActive(updated.getActive());
        return productRepository.save(existing);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}