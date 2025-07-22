package com.gg.gong9.product.controller;

import com.gg.gong9.global.security.jwt.CustomUserDetails;
import com.gg.gong9.product.controller.dto.*;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Product saved = productService.createProduct(requestDto, files, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ProductCreateResponseDto(saved.getId(), "상품 등록이 완료되었습니다."));
    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ProductDetailResponseDto response = productService.getProductDetail(productId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    // 상품 목록 조회
    @GetMapping
    public ResponseEntity<List<ProductListResponseDto>> getProductList(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ProductListResponseDto> response = productService.getProductList(userDetails.getUser());
        return ResponseEntity.ok(response);
    }


    // 상품 수정
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestPart("RequestDto") ProductUpdateRequestDto RequestDto,
            @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles,
            @RequestParam(value = "deleteImgIds", required = false) List<Long> deleteImgIds,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        productService.updateProduct(productId, RequestDto, newFiles, deleteImgIds, userDetails.getUser());
        return ResponseEntity.ok(new ProductResponse("상품 수정이 완료되었습니다."));
    }

    // 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductResponse> deleteProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        productService.deleteProduct(productId, userDetails.getUser());
        return ResponseEntity.ok(new ProductResponse("상품이 삭제되었습니다."));
    }

}
