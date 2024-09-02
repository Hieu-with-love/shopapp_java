package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public void createCategory(CategoryDTO category) {
        Category newCategory = Category.builder()
                        .name(category.getName()).build();
        categoryRepository.save(newCategory);
    }

    @Override
    public void updateCategory(long categoryId, CategoryDTO category) {
        Category existsingCategory = getCategoryById(categoryId);
        existsingCategory.setName(category.getName());

        categoryRepository.save(existsingCategory);
    }

    @Override
    public void deleteCategory(long id) {
        categoryRepository.deleteById(id);
    }
}
