package com.gg.gong9.minibuy.controller;


import com.gg.gong9.global.enums.Category;
import com.gg.gong9.global.enums.BuyStatus;
import com.gg.gong9.global.security.jwt.CustomUserDetails;
import com.gg.gong9.minibuy.controller.dto.*;
import com.gg.gong9.minibuy.service.MiniBuyService;
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
@RequestMapping("/mini-buys")
public class MiniBuyController {

    private final MiniBuyService miniBuyService;

    @PostMapping
    public ResponseEntity<MiniBuyCreateResponseDto> createMiniBuy(
            @Valid @RequestPart("requestDto") MiniBuyCreateRequestDto requestDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long miniBuyId = miniBuyService.createMiniBuy(requestDto, file, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MiniBuyCreateResponseDto(miniBuyId,"소량공구 등록을 완료했습니다."));
    }

    @GetMapping("/{miniBuyId}")
    public ResponseEntity<MiniBuyDetailResponseDto> getMiniBuyDetail(
            @PathVariable("miniBuyId") Long miniBuyId){
        MiniBuyDetailResponseDto response = miniBuyService.getMiniBuyDetail(miniBuyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<MiniBuyCategoryResponseDto>> getMiniBuyCategory(
            @PathVariable("category") Category category
    ){
        List<MiniBuyCategoryResponseDto> response = miniBuyService.getMiniBuyCategoryList(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/urgent")
    public ResponseEntity<List<MiniBuyUrgentListResponseDto>> getMiniBuyListUrgent(){
        List<MiniBuyUrgentListResponseDto> response = miniBuyService.getMiniBuyUrgentList(BuyStatus.RECRUITING);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{miniBuyId}")
    public ResponseEntity<MiniBuyResponse> updateMiniBuy(
            @PathVariable("miniBuyId") Long miniBuyId,
            @Valid @RequestPart("requestDto") MiniBuyUpdateRequestDto requestDto,
            @RequestPart(value = "file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        miniBuyService.updateMiniBuy(miniBuyId, requestDto, file, userDetails.getUser());
        return ResponseEntity.ok(new MiniBuyResponse("소량공구 수정이 완료되었습니다."));
    }

    @PutMapping("/{miniBuyId}/cancel")
    public ResponseEntity<MiniBuyResponse> cancelMiniBuy(
            @PathVariable Long miniBuyId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        miniBuyService.cancelMiniBuy(miniBuyId, userDetails.getUser());
        return ResponseEntity.ok(new MiniBuyResponse("소량 공동구매가 취소되었습니다."));
    }

    @GetMapping("/me")
    public ResponseEntity<List<MiniBuyListResponseDto>> getMiniBuyList(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        List<MiniBuyListResponseDto> response = miniBuyService.getMiniBuyList(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{miniBuyId}")
    public ResponseEntity<MiniBuyResponse> deleteMiniBuy(
            @PathVariable("miniBuyId") Long miniBuyId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        miniBuyService.deleteMiniBuy(miniBuyId, userDetails.getUser());
        return ResponseEntity.ok(new MiniBuyResponse("해당 소량 공구가 삭제되었습니다."));
    }
}


