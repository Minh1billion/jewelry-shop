package pixelism.jewelryshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pixelism.jewelryshop.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}