package com.project.shopapp.repositories;

import com.project.shopapp.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    // Lay ra danh sanh cac anh cua mot san pham
    List<ProductImage> findByProductId(Long productId);
}
