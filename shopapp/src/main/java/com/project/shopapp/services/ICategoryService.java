package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ICategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(long id);
    void createCategory(CategoryDTO category);
    void updateCategory(long id, CategoryDTO category);
    void deleteCategory(long id);
}
