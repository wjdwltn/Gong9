//package com.gg.gong9.coupon.service;
//
//import com.gg.gong9.coupon.controller.dto.CouponCreateRequestDto;
//import com.gg.gong9.coupon.entity.Coupon;
//import com.gg.gong9.coupon.entity.CouponIssue;
//import com.gg.gong9.coupon.repository.CouponIssueRepository;
//import com.gg.gong9.coupon.repository.CouponRepository;
//import com.gg.gong9.minibuy.repository.MiniBuyRepository;
//import com.gg.gong9.user.entity.Address;
//import com.gg.gong9.user.entity.User;
//import com.gg.gong9.user.entity.UserRole;
//import com.gg.gong9.user.repository.UserRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.util.StopWatch;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@Slf4j
//@SpringBootTest
//@ActiveProfiles("test")
//class CouponIssueServiceLuaTest {
//
//    private static final int THREAD_COUNT = 1000;
//    private static final int COUPON_QUANTITY = 10;
//
//    @Autowired private CouponIssueService couponIssueService;
//    @Autowired private UserRepository userRepository;
//    @Autowired private CouponRepository couponRepository;
//    @Autowired private CouponIssueRepository couponIssueRepository;
//    @Autowired private MiniBuyRepository miniBuyRepository;
//    @Autowired private CouponService couponService;
//
//    private Long couponId;
//
//    @BeforeEach
//    void setUp() {
//        clearDatabase();
//        this.couponId = createTestCoupon().getId();
//    }
//
//    @Test
//    void CouponIssueServiceTest() throws Exception {
//        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
//        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//
//        for (int i = 0; i < THREAD_COUNT; i++) {
//            final int index = i;
//            executor.submit(() -> {
//                try {
//                    User user = createTestUser(index);
//                    couponIssueService.issueCoupon(couponId, user);
//                } catch (Exception e) {
//                    log.warn("발급 실패: {}", e.getMessage());
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//        executor.shutdown();
//        executor.awaitTermination(1, TimeUnit.MINUTES);
//
//        stopWatch.stop();
//
//        List<CouponIssue> issued = couponIssueRepository.findAll();
//        Coupon coupon = couponRepository.findById(couponId)
//                .orElseThrow(() -> new IllegalStateException("쿠폰이 존재하지 않습니다."));
//
//        log.info("🔥🔥🔥 테스트 결과 🔥🔥🔥");
//        log.info("총 요청 수: {}", THREAD_COUNT);
//        log.info("발급된 수: {}", issued.size());
//        log.info("남은 재고: {}", coupon.getRemainQuantity());
//        log.info("수행 시간: {} ms", stopWatch.getTotalTimeMillis());
//
//        assertThat(issued.size()).isLessThanOrEqualTo(COUPON_QUANTITY);
//    }
//
//    private void clearDatabase() {
//        miniBuyRepository.deleteAllInBatch();
//        couponIssueRepository.deleteAllInBatch();
//        couponRepository.deleteAllInBatch();
//        userRepository.deleteAllInBatch();
//    }
//
//    private Coupon createTestCoupon() {
//        User admin = userRepository.save(
//                new User("admin", "admin@mail.com", "123456789", "01000000000",
//                        Address.builder().build(), null, UserRole.ADMIN)
//        );
//
//        CouponCreateRequestDto dto = new CouponCreateRequestDto(
//                "선착순 쿠폰(Lua 사용)", COUPON_QUANTITY, 1500, 10000,
//                LocalDateTime.now(), LocalDateTime.now().plusDays(1)
//        );
//
//        return couponService.createCoupon(dto, admin);
//    }
//
//    private User createTestUser(int index) {
//        return userRepository.save(
//                new User("user" + index, "user" + index + "@mail.com", "1234" + index,
//                        "0101234567" + index, Address.builder().build(), null, UserRole.USER)
//        );
//    }
//}
