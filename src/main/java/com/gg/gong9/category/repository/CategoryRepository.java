package com.gg.gong9.category.repository;

import com.gg.gong9.category.entity.Category;
import com.gg.gong9.category.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryType(CategoryType categoryType);

    boolean existsByCategoryType(CategoryType type);
}
