package com.gg.gong9.product.service;

import com.gg.gong9.global.exception.exceptions.product.ProductException;
import com.gg.gong9.product.controller.dto.*;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.repository.ProductRepository;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.entity.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.gg.gong9.global.exception.exceptions.product.ProductExceptionMessage.*;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImgService productImgService;

    // 상품 등록
    @Transactional
    public Product createProduct(ProductCreateRequestDto dto, List<MultipartFile> files, User user) {
        if (user.getUserRole() != UserRole.ADMIN) {
            throw new ProductException(NOT_ADMIN);
        }
        Product product = Product.create(dto, user);
        productImgService.saveProductImgs(product, files);
        return productRepository.save(product);
    }

    // 상품 상세 조회
    public ProductDetailResponseDto getProductDetail(Long productId, User user) {
        Product product = getProductOrThrow(productId);
        validateProductOwner(product, user);
        return ProductDetailResponseDto.from(product);
    }

    // 상품 목록 조회
    public List<ProductListResponseDto> getProductList(User user) {
        return productRepository.findAll().stream()
                .map(ProductListResponseDto::new)
                .collect(Collectors.toList());
    }

    // 상품 수정
    @Transactional
    public void updateProduct(Long productId, ProductUpdateRequestDto dto, List<MultipartFile> newFiles, List<Long> deleteImgIds, User user) {
        Product product = getProductOrThrow(productId);
        validateProductOwner(product, user);

        product.update(
                dto.productName(),
                dto.description(),
                dto.price(),
                dto.category()
        );


        deleteProductImagesIfNecessary(deleteImgIds);
        addNewProductImagesIfPresent(product, newFiles);

    }

    @Transactional
    public void deleteProduct(Long productId, User user) {

        Product product = getProductOrThrow(productId);
        validateProductOwner(product, user);
        productRepository.delete(product);
    }


    public Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));
    }

    private void deleteProductImagesIfNecessary(List<Long> deleteImgIds) {
        if (deleteImgIds != null && !deleteImgIds.isEmpty()) {
            productImgService.deleteProductImgsByIds(deleteImgIds);
        }
    }

    private void addNewProductImagesIfPresent(Product product, List<MultipartFile> newFiles) {
        if (newFiles != null && newFiles.stream().anyMatch(file -> !file.isEmpty())) {
            productImgService.saveProductImgs(product, newFiles);
        }
    }

    private void validateProductOwner(Product product, User user) {
        if (!product.getUser().getId().equals(user.getId())) {
            throw new ProductException(NO_PERMISSION);
        }
    }



}


