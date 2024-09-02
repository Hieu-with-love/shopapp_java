package com.project.shopapp.repositories;

import com.project.shopapp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Method custom find Product by Id
    boolean existsByName(String name);
    // pageable
    //Page<Product> findAllByCategory(Pageable pageable); // phan trang
}
