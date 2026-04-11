package pixelism.jewelryshop.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pixelism.jewelryshop.Category;
import pixelism.jewelryshop.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true AND p.id IN :ids
    """)
    List<Product> findActiveByIds(List<Long> ids);

    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true
        ORDER BY SIZE(p.orderItems) DESC
    """)
    List<Product> findTopSellingProducts(org.springframework.data.domain.Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true
        ORDER BY p.createdAt DESC
    """)
    List<Product> findNewestProducts(org.springframework.data.domain.Pageable pageable);


    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByActiveTrueAndCategory(Category category, Pageable pageable);

    @Query(value = """
    SELECT * FROM products p
    WHERE p.active = true
    AND (:keyword = '' OR 
        LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:categoryId IS NULL OR p.category_id = :categoryId)
    AND (CAST(:minPrice AS numeric) IS NULL OR p.price >= CAST(:minPrice AS numeric))
    AND (CAST(:maxPrice AS numeric) IS NULL OR p.price <= CAST(:maxPrice AS numeric))
""", countQuery = """
    SELECT COUNT(*) FROM products p
    WHERE p.active = true
    AND (:keyword = '' OR 
        LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:categoryId IS NULL OR p.category_id = :categoryId)
    AND (CAST(:minPrice AS numeric) IS NULL OR p.price >= CAST(:minPrice AS numeric))
    AND (CAST(:maxPrice AS numeric) IS NULL OR p.price <= CAST(:maxPrice AS numeric))
""", nativeQuery = true)
    Page<Product> findWithFilters(
            @Param("keyword")    String keyword,
            @Param("categoryId") Long categoryId,
            @Param("minPrice")   BigDecimal minPrice,
            @Param("maxPrice")   BigDecimal maxPrice,
            Pageable pageable
    );
}