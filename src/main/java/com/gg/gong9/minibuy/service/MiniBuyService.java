package com.gg.gong9.minibuy.service;

import com.gg.gong9.global.enums.Category;
import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.global.exception.exceptions.minibuy.MiniBuyException;
import com.gg.gong9.global.exception.exceptions.minibuy.MiniBuyExceptionMessage;
import com.gg.gong9.global.utils.s3.S3Service;
import com.gg.gong9.minibuy.controller.command.MiniBuyUpdateCommand;
import com.gg.gong9.minibuy.controller.dto.*;
import com.gg.gong9.minibuy.entity.MiniBuy;
import com.gg.gong9.minibuy.repository.MiniBuyRepository;
import com.gg.gong9.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MiniBuyService{

    private final MiniBuyRepository miniBuyRepository;
    private final S3Service s3Service;

    // 소량공구 등록
    @Transactional
    public Long createMiniBuy(MiniBuyCreateRequestDto dto, MultipartFile file, User user){

        String imageUrl = (file != null && !file.isEmpty())
                ? s3Service.uploadFile("miniBuy", file)
                : null;

        MiniBuy miniBuy = MiniBuy.builder()
                .productName(dto.productName())
                .productImg(imageUrl)
                .description(dto.description())
                .price(dto.price())
                .category(dto.category())
                .targetCount(dto.targetCount())
                .startAt(dto.startAt())
                .endAt(dto.endAt())
                .user(user)
                .build();

        miniBuyRepository.save(miniBuy);
        return miniBuy.getId();
    }

    // 소량공구 상세조회
    public MiniBuyDetailResponseDto getMiniBuyDetail(Long miniBuyId) {
        MiniBuy miniBuy = getMiniBuyOrThrow(miniBuyId);
        int joinedCount = 0;
        return MiniBuyDetailResponseDto.from(miniBuy);
    }

    // 카테고리별 소량공구 목록 조회
    public List<MiniBuyCategoryResponseDto> getMiniBuyCategoryList(Category category) {
        List<MiniBuy> miniBuys = miniBuyRepository.findByCategory(category);

        return miniBuys.stream()
                .map(miniBuy -> MiniBuyCategoryResponseDto.from(miniBuy, 0))
                .collect(Collectors.toList());
    }

    // 마감 임박 소량공구 목록 조회
    public List<MiniBuyUrgentListResponseDto> getMiniBuyUrgentList(BuyStatus status) {
        List<MiniBuy> miniBuys = miniBuyRepository.findAllByStatusOrderByEndAtAsc(status);

        return miniBuys.stream()
                .map(miniBuy -> MiniBuyUrgentListResponseDto.from(miniBuy, 0))
                .collect(Collectors.toList());
    }

    // 소량공구 수정
    @Transactional
    public void updateMiniBuy(Long miniBuyId, MiniBuyUpdateRequestDto dto, MultipartFile file, User user) {
        MiniBuy miniBuy = getMiniBuyOrThrow(miniBuyId);
        validateMiniBuyOwner(miniBuy, user);

        updateMiniBuyImage(file, miniBuy);

        MiniBuyUpdateCommand command = new MiniBuyUpdateCommand(
                dto.productName(),
                dto.description(),
                dto.price(),
                dto.category(),
                dto.targetCount(),
                dto.startAt(),
                dto.endAt()
        );

        miniBuy.update(command);
    }

    // 소량공구 취소
    @Transactional
    public void cancelMiniBuy(Long miniBuyId, User user) {
        MiniBuy miniBuy = getMiniBuyOrThrow(miniBuyId);
        validateMiniBuyOwner(miniBuy, user);
        miniBuy.cancel();
    }

    // 내가 등록한 소량공구 목록 조회
    public List<MiniBuyListResponseDto> getMiniBuyList(User user) {
        return miniBuyRepository.findByUserId(user.getId()).stream()
                .map(miniBuy -> MiniBuyListResponseDto.from(miniBuy,0))
                .collect(Collectors.toList());
    }

    // 소량공구 삭제
    @Transactional
    public void deleteMiniBuy(Long miniBuyId, User user) {
        MiniBuy miniBuy = getMiniBuyOrThrow(miniBuyId);
        validateMiniBuyOwner(miniBuy, user);

        s3Service.deleteFile(miniBuy.getProductImg());

        miniBuyRepository.delete(miniBuy);
    }


    private MiniBuy getMiniBuyOrThrow(Long miniBuyId){
        return miniBuyRepository.findById(miniBuyId)
                .orElseThrow(()->new MiniBuyException(MiniBuyExceptionMessage.NOT_FOUND_MINI_BUY));
    }

    private void validateMiniBuyOwner(MiniBuy miniBuy,User user){
        if (!miniBuy.getUser().getId().equals(user.getId())){
            throw new MiniBuyException (MiniBuyExceptionMessage.NO_PERMISSION_MINI_BUY);
        }
    }

    private void updateMiniBuyImage(MultipartFile file, MiniBuy miniBuy) {
        if (file != null && !file.isEmpty()) {
            String oldImg = miniBuy.getProductImg();
            if (oldImg != null) {
                s3Service.deleteFile(oldImg);
            }

            String newImageUrl = s3Service.uploadFile("miniBuy", file);
            miniBuy.updateProductImage(newImageUrl);
        }
    }


}





