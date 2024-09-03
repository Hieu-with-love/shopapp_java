package com.project.shopapp.controllers;

import com.github.javafaker.Faker;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<?> getAllProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        try {
            // Lấy ra các sản phẩm tại
            PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
            Page<ProductResponse> productResponsePage = productService.getAllProducts(pageRequest);
            int totalPages = productResponsePage.getTotalPages();
            List<ProductResponse> products = productResponsePage.getContent();
            return ResponseEntity.ok(ProductListResponse.builder()
                   .products(products)
                   .numberOfPages(totalPages)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductWithId(@PathVariable("id") Long productId) {
        try {
            Product existingProduct = productService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.getFromProduct(existingProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // tao du lieu tho
    @PostMapping("")
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result
    ) {
        try {
            // Validation check
            if (result.hasErrors()) {
                List<String> lstErrorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                return ResponseEntity.badRequest().body(lstErrorMessage);
            }

            // store product
            Product product = productService.createProduct(productDTO);

            return ResponseEntity.ok("Product created successfully. " + product);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @Valid @ModelAttribute("files") List<MultipartFile> files
    ) {
        try {

            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
                return ResponseEntity.badRequest().body("Number of images must be less than or equal to 5.");
            }

            Product existingProduct = productService.getProductById(productId);
            List<ProductImage> lstImages = new ArrayList<>();
            for (MultipartFile file : files) {
                // Nếu kích thước file == 0 -> trống -> continue
                if (file.getSize() == 0) {
                    continue;
                }
                // Kiểm tra kích thước file và định dạng
                // Kích thước > 10MB thì ném ra ngoại lệ
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is too large ! Maximum size is 10MB");
                }

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File must be an image");
                }
                // Lưu file và cập nhật thumbnail trong DTO
                String fileName = storeFile(file);
                // store object into db
                ProductImage productImage = productService.createProductImage(productId, ProductImageDTO
                        .builder()
                        .productId(existingProduct.getId())
                        .imageUrl(fileName)
                        .build()
                );
                lstImages.add(productImage);
            }
            return ResponseEntity.ok(lstImages);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // check is image file?
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getOriginalFilename();
        return contentType != null && contentType.startsWith("image/");
    }

    // Method store file
    private String storeFile(MultipartFile file) throws IOException{
        if (!isImageFile(file)) {
            throw new IOException("Invalid image file format");
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Using UUID class to create filename unique
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        java.nio.file.Path uploadDir = Paths.get("upload");
        // Check and create when non exists
        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }
        // Full Path to file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @Valid @RequestBody ProductDTO productDTO,
            @PathVariable("id") Long productId,
            BindingResult result
    ){

        try{
            return ResponseEntity.ok(productService.updateProduct(productId, productDTO));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long productId){
        try{
            productService.deleteProduct(productId);
            return ResponseEntity.ok("Delete product successfully !");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // API tạm thơì phục vụ nhu cầu, làm xong tắt đi dể tránh bị lỗi
    //@PostMapping("/generateFakerProducts")
    private ResponseEntity<?> generateFakerProducts(){
        Faker faker = new Faker();
        for (int i = 0; i < 1_000; i++){
            String productName = faker.commerce().productName();
            if (productService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price(faker.number().numberBetween(10, 90_000_000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long)faker.number().numberBetween(1,4))
                    .build();

            try{
                productService.createProduct(productDTO);
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake products created successfully !");
    }
}
