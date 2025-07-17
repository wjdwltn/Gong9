package com.gg.gong9.product.service;

import com.gg.gong9.category.entity.Category;
import com.gg.gong9.category.entity.CategoryType;
import com.gg.gong9.category.repository.CategoryRepository;
import com.gg.gong9.global.exception.exceptions.ProductException;
import com.gg.gong9.product.controller.dto.*;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.gg.gong9.global.exception.ExceptionMessage.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImgService productImgService;
    private final CategoryRepository categoryRepository;

    // 상품 등록
    @Transactional
    public ProductCreateResponseDto createProduct(ProductCreateRequestDto dto, List<MultipartFile> files) {
        Category category = getCategory(dto.category());
        Product product = Product.create(dto, category);

        productImgService.saveProductImgs(product, files);
        Product saved = productRepository.save(product);

        return new ProductCreateResponseDto(saved.getId(), "상품 등록이 완료되었습니다.");
    }

    // 상품 상세 조회
    public ProductDetailResponseDto getProductDetail(Long productId) {
        Product product = getProductOrThrow(productId);
        return ProductDetailResponseDto.from(product);
    }

    // 상품 목록 조회
    public List<ProductListResponseDto> getProductList() {
        return productRepository.findAll().stream()
                .map(ProductListResponseDto::new)
                .collect(Collectors.toList());
    }

    // 상품 수정
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequestDto dto, List<MultipartFile> newFiles, List<Long> deleteImgIds) {
        Product product = getProductOrThrow(productId);
        Category category = getCategory(dto.category());

        product.update(
                dto.productName(),
                dto.description(),
                dto.price(),
                category
        );


        deleteProductImagesIfNecessary(deleteImgIds);
        addNewProductImagesIfPresent(product, newFiles);

        return new ProductResponse("상품 정보가 수정되었습니다");
    }

    @Transactional
    public ProductResponse deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

        // 자신이 등록한 상품만 삭제 가능

        productRepository.delete(product);
        return new ProductResponse("상품이 삭제되었습니다.");
    }


    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));
    }

    private Category getCategory(CategoryType categoryType) {
        return categoryRepository.findByCategoryType(categoryType);
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


}


