package com.gg.gong9.product.controller;

import com.gg.gong9.product.controller.dto.*;
import com.gg.gong9.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // 상품 등록
    @PostMapping
    public ResponseEntity<ProductCreateResponseDto> create(
            @Valid @RequestPart("requestDto") ProductCreateRequestDto requestDto,
            @RequestPart("files") List<MultipartFile> files
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(requestDto, files));
    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(
            @PathVariable Long productId
    ) {
        ProductDetailResponseDto response = productService.getProductDetail(productId);
        return ResponseEntity.ok(response);
    }

    // 상품 목록 조회
    @GetMapping
    public ResponseEntity<List<ProductListResponseDto>> getProductList() {
        List<ProductListResponseDto> response = productService.getProductList();
        return ResponseEntity.ok(response);
    }


    // 상품 수정
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestPart("RequestDto") ProductUpdateRequestDto RequestDto,
            @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles,
            @RequestParam(value = "deleteImgIds", required = false) List<Long> deleteImgIds) {

        ProductResponse response = productService.updateProduct(productId, RequestDto, newFiles, deleteImgIds);
        return ResponseEntity.ok(response);
    }

    // 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductResponse> deleteProduct(
            @PathVariable Long productId
    ) {
        ProductResponse response = productService.deleteProduct(productId);
        return ResponseEntity.ok(response);
    }

}
