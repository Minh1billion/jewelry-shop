package pixelism.jewelryshop;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pixelism.jewelryshop.repositories.CategoryRepository;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
}