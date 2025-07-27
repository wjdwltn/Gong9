package com.gg.gong9.groupbuy.service;

import com.gg.gong9.global.exception.exceptions.groupbuy.GroupBuyException;
import com.gg.gong9.global.exception.exceptions.product.ProductException;
import com.gg.gong9.groupbuy.controller.command.GroupBuyUpdateCommand;
import com.gg.gong9.groupbuy.controller.dto.*;
import com.gg.gong9.groupbuy.entity.GroupBuy;
import com.gg.gong9.groupbuy.entity.Status;
import com.gg.gong9.groupbuy.handler.GroupBuyStatusHandler;
import com.gg.gong9.groupbuy.repository.GroupBuyRepository;
import com.gg.gong9.groupbuy.controller.dto.GroupBuyListResponseDto;
import com.gg.gong9.notification.sms.service.SmsService;
import com.gg.gong9.order.repository.OrderRepository;
import com.gg.gong9.product.entity.Category;
import com.gg.gong9.product.entity.Product;
import com.gg.gong9.product.repository.ProductRepository;
import com.gg.gong9.user.entity.User;
import com.gg.gong9.user.entity.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.gg.gong9.global.exception.exceptions.groupbuy.GroupBuyExceptionMessage.*;
import static com.gg.gong9.global.exception.exceptions.product.ProductExceptionMessage.NO_PERMISSION_PRODUCT;
import static com.gg.gong9.global.exception.exceptions.product.ProductExceptionMessage.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupBuyService {

    private final GroupBuyRepository groupBuyRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final SmsService smsService;
    private final GroupBuyStatusHandler groupBuyStatusHandler;

    // 공구 등록
    @Transactional
    public Long createGroupBuy(GroupBuyCreateRequestDto dto, User user) {
        Product product = getProductOrThrow(dto.productId());

        validateSeller(user);
        validateProductOwner(product, user);

        GroupBuy groupBuy = GroupBuy.create(dto,product,user);
        groupBuyRepository.save(groupBuy);
        return groupBuy.getId();
    }

    // 공구 상세 조회
    public GroupBuyDetailResponseDto getGroupBuyDetail(Long groupBuyId) {
        GroupBuy groupBuy = getGroupBuyOrThrow(groupBuyId);

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
    public void updateGroupBuy(Long groupBuyId, GroupBuyUpdateRequestDto dto, User user) {
        GroupBuy groupBuy = getGroupBuyOrThrow(groupBuyId);

        validateOwner(groupBuy, user);
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
    public void cancelGroupBuy(Long groupBuyId, User user) {
        GroupBuy groupBuy = getGroupBuyOrThrow(groupBuyId);

        validateOwner(groupBuy, user);
        validateNotEnded(groupBuy);

        groupBuy.cancel();

        //모집중일때만 환불 및 취소 메세지 보내기
        groupBuyStatusHandler.handleCancelled(groupBuy);
    }


    // 내가 등록한 공구 목록 조회
    public List<GroupBuyListResponseDto> getGroupBuyList(User user) {
        return groupBuyRepository.findByUserId(user.getId()).stream()
                .map(groupBuy -> new GroupBuyListResponseDto(groupBuy, 0)) // joinedQuantity 계산 구매파트 구현 후 수정 (0으로 하드코딩)
                .collect(Collectors.toList());

    }

    // 공구 등록 삭제
    @Transactional
    public void deleteGroupBuy(Long groupBuyId, User user) {

        GroupBuy groupBuy = getGroupBuyOrThrow(groupBuyId);

        validateOwner(groupBuy, user);
        groupBuyRepository.delete(groupBuy);
    }

    private void validateSeller(User user) {
        if (!user.getUserRole().equals(UserRole.ADMIN)) {
            throw new GroupBuyException(ONLY_SELLER_CAN_REGISTER);
        }
    }

    private void validateOwner(GroupBuy groupBuy, User user) {
        if (!groupBuy.getUser().getId().equals(user.getId())) {
            throw new GroupBuyException(NO_PERMISSION_GROUPBUY);
        }
    }

    private void validateNotEnded(GroupBuy groupBuy) {
        if (groupBuy.getStatus() == Status.COMPLETED || groupBuy.getStatus() == Status.CANCELED) {
            throw new GroupBuyException(ALREADY_ENDED);
        }
    }

    private GroupBuy getGroupBuyOrThrow(Long groupBuyId) {
        return groupBuyRepository.findById(groupBuyId)
                .orElseThrow(() -> new GroupBuyException(NOT_FOUND_GROUPBUY));
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));
    }

    private void validateProductOwner(Product product, User user) {
        if(!product.getUser().getId().equals(user.getId())) {
            throw new ProductException(NO_PERMISSION_PRODUCT);
        }
    }



    // 내 공구 통계 조회
}
