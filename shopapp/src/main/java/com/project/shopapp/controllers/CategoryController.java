package com.project.shopapp.controllers;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.services.CategoryService;
import com.project.shopapp.services.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.ErrorManager;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/categories") // http://localhost:8088/api/v1/categories
public class CategoryController {

    private final CategoryService categoryService;

    // Hiển thị tất cả các categories
    @GetMapping() //http://localhost:8088/api/v1/categories
    public ResponseEntity<?> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping("") //http://localhost:8088/api/v1/categories?page=1&limit=10
    public ResponseEntity<?> createCategories(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult result){
        try{
            if (result.hasErrors()){
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }

            categoryService.createCategory(categoryDTO);

            return ResponseEntity.ok("Category created successfully " + categoryDTO);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategories(@RequestBody CategoryDTO categoryDTO,@PathVariable Long id){
        try{
            categoryService.updateCategory(id, categoryDTO);
            return ResponseEntity.ok("Update category successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategories(@PathVariable Long id){
        try{
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Delete category successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
