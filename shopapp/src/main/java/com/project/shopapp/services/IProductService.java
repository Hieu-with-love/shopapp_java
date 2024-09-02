package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.project.shopapp.models.*;

import java.awt.print.Pageable;
import java.util.List;

@Service
public interface IProductService {
    List<Product> getAllProducts();
    Product getProductById(long id) throws Exception;
    Page<ProductResponse> getAllProducts(PageRequest pageRequest);
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    Product updateProduct(long productId, ProductDTO productDTO) throws Exception;
    void deleteProduct(long productId) throws Exception;
    boolean existsByName(String name);
    ProductImage createProductImage(long productId, ProductImageDTO productImageDTO) throws Exception;

}
