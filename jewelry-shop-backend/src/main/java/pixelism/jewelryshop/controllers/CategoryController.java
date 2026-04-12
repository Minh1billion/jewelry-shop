package pixelism.jewelryshop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.features.Category;
import pixelism.jewelryshop.repositories.CategoryRepository;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final Category category = new Category();

    @GetMapping
    public List<Category> getAll() {
        return category.getAll(categoryRepository);
    }
}