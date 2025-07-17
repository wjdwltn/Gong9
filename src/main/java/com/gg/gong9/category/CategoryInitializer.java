package com.gg.gong9.category;

import com.gg.gong9.category.entity.Category;
import com.gg.gong9.category.entity.CategoryType;
import com.gg.gong9.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) {
        for (CategoryType type : CategoryType.values()) {
            if (!categoryRepository.existsByCategoryType(type)) {
                categoryRepository.save(new Category(type));
            }
        }
    }
}