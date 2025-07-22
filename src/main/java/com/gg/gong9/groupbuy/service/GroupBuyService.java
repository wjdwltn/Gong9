package com.gg.gong9.groupbuy.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.gg.gong9.groupbuy.controller.command.GroupBuyUpdateCommand;
import com.gg.gong9.groupbuy.controller.dto.*;
import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.groupbuy.entity.Status;
import com.gg.gong9.groupbuy.repository.GroupBuyRepository;
import com.gg.gong9.groupbuy.controller.dto.GroupBuyListResponseDto;
import com.gg.gong9.product.entity.Category;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.repository.ProductRepository;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.entity.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupBuyService {

    private final GroupBuyRepository groupBuyRepository;
    private final ProductRepository productRepository;

    // 공구 등록
    @Transactional
    public Long createGroupBuy(GroupBuyCreateRequestDto dto, User user) {
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new NotFoundException("상품이 존재하지 않습니다."));

        //판매자만 등록 가능
        if(!user.getUserRole().equals(UserRole.ADMIN)){
            throw new UnsupportedOperationException("공구는 판매자의 경우만 등록할 수 있습니다.");
        }

// 자신의 상품의 경우에만

        GroupBuy groupBuy = GroupBuy.create(dto,product,user);
        groupBuyRepository.save(groupBuy);
        return groupBuy.getId();
    }

    // 공구 상세 조회
    public GroupBuyDetailResponseDto getGroupBuyDetail(Long GroupBuyId) {
        GroupBuy groupBuy = groupBuyRepository.findById(GroupBuyId)
                .orElseThrow(() -> new NotFoundException("해당 공동구매가 존재하지 않습니다."));

        int joinedQuantity = 0; // 주문 수량 합계 (구매 구현 후 추가예정 임시 0으로)
        return GroupBuyDetailResponseDto.from(groupBuy, joinedQuantity);
    }

    // 카테고리별 공구 목록 조회
    public List<GroupBuyCategoryListResponseDto> getGroupBuyCategoryList(Category category){
        List<GroupBuy> groupBuys = groupBuyRepository.findByProductCategory(category);

        return groupBuys.stream()
                .map(groupBuy -> new GroupBuyCategoryListResponseDto(groupBuy,0))
                .collect(Collectors.toList());
    }

    // 마감 임박 공구 목록 조회
    public List<GroupBuyUrgentListResponseDto> getGroupBuyUrgentList(Status status){
        List<GroupBuy> groupBuys = groupBuyRepository.findAllByStatusOrderByEndAtAsc(status);

        return groupBuys.stream()
                .map(groupBuy->new GroupBuyUrgentListResponseDto(groupBuy, 0))
                .collect(Collectors.toList());
    }

    // 공구 정보 수정
    @Transactional
    public void updateGroupBuy(Long GroupBuyId, GroupBuyUpdateRequestDto dto, User user) {
        GroupBuy groupBuy = groupBuyRepository.findById(GroupBuyId)
                .orElseThrow(()->new NotFoundException("해당 공동구매가 존재하지 않습니다."));

        // 자신이 등록한 공동구매가 아니면 수정불가
        if(!groupBuy.getUser().getId().equals(user.getId())){
            throw new UnsupportedOperationException("공구를 수정할 권한이 없습니다.");
        }

        groupBuy.updateStatus();
        // 현재 결제된 수량을 가져오기 (결제 구현 후 수정예정. 임시 0으로 하드코딩)
        int paidQuantity = 0;

        GroupBuyUpdateCommand command = new GroupBuyUpdateCommand(
                dto.totalQuantity(),
                dto.limitQuantity(),
                dto.startAt(),
                dto.endAt(),
                paidQuantity
        );

        groupBuy.update(command);
    }

    // 공구 진행 취소
    @Transactional
    public void cancelGroupBuy(Long groupById, User user) {
        GroupBuy groupBuy = groupBuyRepository.findById(groupById)
                .orElseThrow(()->new NotFoundException("해당 공동구매가 존재하지 않습니다."));
        if(!groupBuy.getUser().getId().equals(user.getId())){
            throw new UnsupportedOperationException("해당 공동구매를 취소할 권한이 없습니다.");
        }

        if (groupBuy.getStatus() == Status.COMPLETED || groupBuy.getStatus() == Status.CANCELED) {
            throw new IllegalStateException("이미 종료된 공동구매는 취소할 수 없습니다.");
        }

        groupBuy.cancel();
    }


    // 내가 등록한 공구 목록 조회
    public List<GroupBuyListResponseDto> getGroupBuyList(User user) {
        return groupBuyRepository.findByUserId(user.getId()).stream()
                .map(groupBuy -> new GroupBuyListResponseDto(groupBuy, 0)) // joinedQuantity 계산 구매파트 구현 후 수정 (0으로 하드코딩)
                .collect(Collectors.toList());

    }

    // 공구 등록 삭제
    @Transactional
    public void DeleteGroupBuy(Long groupById, User user) {

        GroupBuy groupBuy = groupBuyRepository.findById(groupById)
                .orElseThrow(()->new NotFoundException("해당 공동구매가 존재하지 않습니다."));

        if(!groupBuy.getUser().getId().equals(user.getId())){
            throw new UnsupportedOperationException("공구를 삭제할 권한이 없습니다.");
        }
        groupBuyRepository.delete(groupBuy);
    }

    // 내 공구 통계 조회
}
