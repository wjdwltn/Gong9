package com.gg.gong9.product.service;

import com.gg.gong9.global.exception.exceptions.product.ProductException;
import com.gg.gong9.global.exception.exceptions.product.ProductExceptionMessage;
import com.gg.gong9.global.utils.s3.S3Service;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.entity.ProductImg;
import com.gg.gong9.product.repository.ProductImgRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImgService {

    private final S3Service s3Service;
    private final ProductImgRepository productImgRepository;

    // 이미지 저장 (다중)
    @Transactional
    public List<ProductImg> saveProductImgs(Product product, List<MultipartFile> files) {
        List<ProductImg> savedImgs = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = s3Service.uploadFile("products", file);
            ProductImg productImg = ProductImg.createProductImg(product, url);
            savedImgs.add(productImgRepository.save(productImg));
        }
        return savedImgs;
    }

    // 이미지 조회
    public List<ProductImg> getProductImgs(Long productId) {
        List<ProductImg> productImgs = productImgRepository.findAllByProductId(productId);
        if (productImgs.isEmpty()) {
            throw new ProductException(ProductExceptionMessage.PRODUCT_IMAGE_NOT_FOUND);
        }
        return productImgs;
    }

    // 이미지 수정
    @Transactional
    public List<ProductImg> updateProductImgs(Product product, List<MultipartFile> files) {
        deleteProductImgs(product.getId());
        return saveProductImgs(product, files);
    }

    // 이미지 삭제
    @Transactional
    public void deleteProductImgs(Long productId) {
        List<ProductImg> productImgs = getProductImgs(productId);
        productImgRepository.deleteAll(productImgs);
    }

    // 이미지 일괄 삭제
    @Transactional
    public void deleteProductImgsByIds(List<Long> imgIds) {
        if (imgIds == null || imgIds.isEmpty()) return;
        List<ProductImg> imgsToDelete = productImgRepository.findAllById(imgIds);
        productImgRepository.deleteAll(imgsToDelete);
    }
}
