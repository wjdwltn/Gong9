//package com.gg.gong9;
//
//import com.gg.gong9.coupon.repository.CouponIssueRepository;
//import com.gg.gong9.coupon.repository.CouponRepository;
//import com.gg.gong9.global.enums.BuyStatus;
//import com.gg.gong9.global.enums.Category;
//import com.gg.gong9.minibuy.entity.MiniBuy;
//import com.gg.gong9.minibuy.repository.MiniBuyRepository;
//import com.gg.gong9.participation.controller.dto.ParticipationCreateRequestDto;
//import com.gg.gong9.participation.repository.ParticipationRepository;
//import com.gg.gong9.participation.service.ParticipationService;
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
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.util.StopWatch;
//
//import java.time.LocalDateTime;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Slf4j
//@SpringBootTest
//@ActiveProfiles("test")
//public class ParticipationServiceTest {
//
//    private static final int THREAD_COUNT = 30;
//    private static final int TARGET_PARTICIPANTS = 10;
//
//    @Autowired
//    private ParticipationService participationService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private MiniBuyRepository miniBuyRepository;
//
//    @Autowired
//    private CouponRepository couponRepository;
//
//    @Autowired
//    private ParticipationRepository participationRepository;
//
//    @Autowired
//    CouponIssueRepository couponIssueRepository;
//
//    private Long miniBuyId;
//
//    @BeforeEach
//    void setUp() {
//        clearDatabase();
//        this.miniBuyId = createTestMiniBuy().getId();
//    }
//
//    @Test
//    void testConcurrentParticipation() throws Exception {
//        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
//        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//
//        for (int i = 0; i < THREAD_COUNT; i++) {
//            final int index = i;
//            ParticipationCreateRequestDto dto = new ParticipationCreateRequestDto(miniBuyId);
//
//            executor.submit(() -> {
//                try {
//                    User user = createTestUser(index);
//                    participationService.createParticipation(user, dto);
//                } catch (Exception e) {
//                    log.warn("참가 실패: {}", dto);
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
//        MiniBuy miniBuy = miniBuyRepository.findById(miniBuyId)
//                .orElseThrow(() -> new IllegalStateException("MiniBuy 존재x"));
//
//        log.info("🔥🔥🔥 테스트 결과 🔥🔥🔥");
//        log.info("총 요청 수: {}", THREAD_COUNT);
//        log.info("참가 성공 인원: {}", miniBuy.getTargetCount());
//        log.info("남은 모집 인원: {}", miniBuy.getRemainCount());
//        log.info("수행 시간: {}", stopWatch.getTotalTimeMillis());
//
//        assertThat(miniBuy.getTargetCount()).isLessThanOrEqualTo(TARGET_PARTICIPANTS);
//    }
//
//    private void clearDatabase() {
//        participationRepository.deleteAllInBatch();
//        couponIssueRepository.deleteAllInBatch();
//        couponRepository.deleteAllInBatch();
//        miniBuyRepository.deleteAllInBatch();
//        userRepository.deleteAllInBatch();
//    }
//
//    private MiniBuy createTestMiniBuy() {
//        User admin = userRepository.save(
//                new User("admin", "admin@mail.com", "123456789", "01000000000",
//                        Address.builder().build(), null, UserRole.ADMIN)
//        );
//
//        MiniBuy miniBuy = MiniBuy.create(
//                "테스트 소량공구",
//                "상품 설명",
//                "dd",
//                1000,
//                Category.FOOD,
//                10,
//                LocalDateTime.now(),
//                LocalDateTime.now().plusDays(1),
//                admin
//        );
//
//        // 테스트용 -> 상태를 바로 RECRUITING로 변경
//        ReflectionTestUtils.setField(miniBuy, "status", BuyStatus.RECRUITING);
//        return miniBuyRepository.save(miniBuy);
//    }
//
//
//    private User createTestUser(int index) {
//        return userRepository.save(
//                new User("user" + index, "user" + index + "@mail.com", "1234" + index,
//                        "0101234567" + index, Address.builder().build(), null, UserRole.USER)
//        );
//    }
//}
