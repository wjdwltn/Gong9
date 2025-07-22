package com.gg.gong9.groupbuy.controller;

import com.gg.gong9.global.security.jwt.CustomUserDetails;
import com.gg.gong9.groupbuy.controller.dto.*;
import com.gg.gong9.groupbuy.entity.Status;
import com.gg.gong9.groupbuy.service.GroupBuyService;
import com.gg.gong9.product.entity.Category;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group-buy")
public class GroupBuyController {

    private final GroupBuyService groupBuyService;

    @PostMapping
    public ResponseEntity<GroupBuyCreateResponseDto> createGroupBuy(
            @Valid @RequestBody GroupBuyCreateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        Long groupBuyId = groupBuyService.createGroupBuy(requestDto, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GroupBuyCreateResponseDto(groupBuyId,"공구 등록이 완료되었습니다."));
    }

    // 공구 상세 조회
    @GetMapping("/{groupBuyId}")
    public ResponseEntity<GroupBuyDetailResponseDto> getGroupBuyDetail(
            @PathVariable Long groupBuyId){
        GroupBuyDetailResponseDto response = groupBuyService.getGroupBuyDetail(groupBuyId);
        return ResponseEntity.ok(response);
    }

    // 카테고리별 공구 목록 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<List<GroupBuyCategoryListResponseDto>> getGroupBuyCategoryList(
            @PathVariable Category category
    ){
        List<GroupBuyCategoryListResponseDto> response = groupBuyService.getGroupBuyCategoryList(category);
        return ResponseEntity.ok(response);
    }

    // 마감 임박 공구 목록 조회
    @GetMapping("/urgent")
    public ResponseEntity<List<GroupBuyUrgentListResponseDto>> getGroupBuyListByUrgent() {
        List<GroupBuyUrgentListResponseDto> response = groupBuyService.getGroupBuyUrgentList(Status.RECRUITING);
        return ResponseEntity.ok(response);
    }

    // 공구 정보 수정
    @PutMapping("/{groupBuyId}")
    public ResponseEntity<GroupBuyResponse> updateGroupBuy(
            @PathVariable Long groupBuyId,
            @Valid @RequestBody GroupBuyUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        groupBuyService.updateGroupBuy(groupBuyId, requestDto, userDetails.getUser());
        return ResponseEntity.ok(new GroupBuyResponse("공구 정보 수정이 완료되었습니다."));
    }

    // 공구 진행 취소
    @PutMapping("{groupBuyId}/cancel")
    public ResponseEntity<GroupBuyResponse> cancelGroupBuy(
            @PathVariable Long groupBuyId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        groupBuyService.cancelGroupBuy(groupBuyId, userDetails.getUser());
        return ResponseEntity.ok(new GroupBuyResponse("공동구매가 취소되었습니다."));
    }

    // 내가 등록한 공구 목록 조회
    @GetMapping("/me")
    public ResponseEntity<List<GroupBuyListResponseDto>> getGroupBuyList(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        List<GroupBuyListResponseDto> response = groupBuyService.getGroupBuyList(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    // 공구 등록 삭제
    @DeleteMapping("/{groupBuyId}")
    public ResponseEntity<GroupBuyResponse> deleteGroupBuy(
            @PathVariable Long groupBuyId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        groupBuyService.deleteGroupBuy(groupBuyId, userDetails.getUser());
        return ResponseEntity.ok(new GroupBuyResponse("공구가 삭제되었습니다."));
    }

    // 내 공구 통계 조회 추후 구현예정
}
