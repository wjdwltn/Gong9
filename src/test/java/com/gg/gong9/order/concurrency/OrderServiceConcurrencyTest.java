//package com.gg.gong9.order.concurrency;
//
//import com.gg.gong9.global.enums.Category;
//import com.gg.gong9.groupbuy.controller.dto.GroupBuyCreateRequestDto;
//import com.gg.gong9.groupbuy.entity.GroupBuy;
//import com.gg.gong9.groupbuy.repository.GroupBuyRepository;
//import com.gg.gong9.order.controller.dto.OrderRequest;
//import com.gg.gong9.order.repository.OrderRepository;
//import com.gg.gong9.order.service.concurrency.jvm.OrderJvmService;
//import com.gg.gong9.order.service.concurrency.redis_lau.OrderLauScriptService;
//import com.gg.gong9.order.service.concurrency.strategy.OrderConcurrencyService;
//import com.gg.gong9.order.service.OrderRedisStockService;
//import com.gg.gong9.order.service.OrderService;
//import com.gg.gong9.product.controller.dto.ProductCreateRequestDto;
//import com.gg.gong9.product.entity.Product;
//import com.gg.gong9.product.repository.ProductRepository;
//import com.gg.gong9.user.entity.Address;
//import com.gg.gong9.user.entity.BankAccount;
//import com.gg.gong9.user.entity.User;
//import com.gg.gong9.user.repository.UserRepository;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.OptimisticLockException;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.orm.ObjectOptimisticLockingFailureException;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static java.util.concurrent.TimeUnit.SECONDS;
//import static org.awaitility.Awaitility.await;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class OrderServiceConcurrencyTest {
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private GroupBuyRepository groupBuyRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private OrderRedisStockService orderRedisStockService;
//
//    @Autowired
//    private OrderConcurrencyService orderConcurrencyService;
//
//    @Autowired
//    private OrderJvmService orderJvmService;
//
//    @Autowired
//    private OrderLauScriptService orderLauScriptService;
//
//    private List<User> testUsers;
//    private GroupBuy testGroupBuy;
//
//
//    @BeforeEach
//    public void setUp() {
//        // 판매자 생성
//        BankAccount bankAccount = BankAccount.builder()
//                .bankName("우리은행")
//                .accountNumber("123-456-789")
//                .build();
//
//        User seller = userRepository.findById(1L)
//                .orElseGet(() -> userRepository.save(
//                        User.createSeller(
//                                "seller",
//                                "seller@test.com",
//                                "password",
//                                "01012345678",
//                                bankAccount
//                        )
//                ));
//
//        // 상품 DTO 생성
//        ProductCreateRequestDto productDto = new ProductCreateRequestDto(
//                "테스트 상품",
//                "테스트 상품 설명",
//                10000,
//                Category.FASHION // 적절한 카테고리 enum 사용
//        );
//
//        // 상품 생성 또는 조회
//        Product product = productRepository.findAll().stream().findFirst()
//                .orElseGet(() -> productRepository.save(
//                        Product.create(productDto, seller)
//                ));
//
//        // 공동구매 DTO 생성
//        GroupBuyCreateRequestDto groupBuyDto = new GroupBuyCreateRequestDto(
//                5, // 총 수량
//                5,   // 1인 제한 수량
//                LocalDateTime.now(),
//                LocalDateTime.now().plusDays(7),
//                product.getId()
//        );
//
//        // 공동구매 생성
//        testGroupBuy = groupBuyRepository.save(
//                GroupBuy.create(groupBuyDto, product, seller)
//        );
//
//        // 테스트용 유저들 100명 생성
//        testUsers = createTestUsers(10);
//
//        // Redis 재고 초기화 50개
//        orderRedisStockService.initStock(testGroupBuy.getId(), 5);
//    }
//
//    private List<User> createTestUsers(int count) {
//        List<User> users = new ArrayList<>();
//
//        for (int i = 1; i <= count; i++) {
//            Address address = Address.builder()
//                    .postcode("06236")
//                    .detail("강남구 역삼동 " + i + "번지")
//                    .build();
//
//            User user = User.createBuyer(
//                    "buyer" + i,
//                    "buyer" + i + "@test.com",
//                    "password" + i,
//                    "0100000" + String.format("%04d", i),
//                    address
//            );
//
//            users.add(user);
//        }
//
//        return userRepository.saveAll(users);
//    }
//
//    @AfterEach
//    public void tearDown() {
//        try {
//            orderRepository.deleteAllInBatch();
////            groupBuyRepository.deleteAllInBatch();
////            productRepository.deleteAllInBatch();
//            userRepository.deleteAllInBatch();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Nested
//    @DisplayName("단일 reentrantLock을 사용해 동시에 주문을 요청한다.")
//    class createOrderTest_reentrantLock {
//
//        @Test
//        @DisplayName("단일 reen_동시에 100명이 공동구매 상품에 주문을 요청한다.")
//        public void createOrderTest() throws InterruptedException {
//            int threadCount = 1000;
//            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//            CountDownLatch latch = new CountDownLatch(threadCount);
//
//            AtomicInteger successCount = new AtomicInteger(0);
//            AtomicInteger failCount = new AtomicInteger(0);
//
//            long startTime = System.currentTimeMillis();
//
//            for(int i=0; i<threadCount; i++){
//                final int index = i;
//                executor.submit(() -> {
//                    try {
//                        OrderRequest request = new OrderRequest(testGroupBuy.getId(), 1);
//                        orderJvmService.createOrder_Reen(testUsers.get(index).getId(), request);
//                        successCount.incrementAndGet();
//                    } catch (Exception e) {
//                        //System.err.println("!!!!!!주문실패!!!!!!!!!!!!!");
//                        failCount.incrementAndGet();
//                    } finally {
//                        latch.countDown();
//                    }
//                });
//            }
//
//            latch.await();
//            executor.shutdown();
//            executor.awaitTermination(1, TimeUnit.MINUTES);
//
//            long endTime = System.currentTimeMillis();
//
//            GroupBuy updatedGroupBuy = groupBuyRepository.findById(testGroupBuy.getId())
//                    .orElseThrow();
//
//            System.out.println("성공 주문 수: " + successCount.get());
//            System.out.println("실패 주문 수: " + failCount.get());
//            System.out.println("남은 수량: " + updatedGroupBuy.getRemainingQuantity());
//            System.out.println("걸린 시간: " + (endTime - startTime) + "ms");
//
//            assertEquals(640, successCount.get() + updatedGroupBuy.getRemainingQuantity());
//            assertTrue(updatedGroupBuy.getRemainingQuantity() >= 0);
//        }
//
//        @Test
//        @DisplayName("단일 reen_동시에 200명이 공동구매 상품에 주문을 요청한다.")
//        public void createOrderTest2() throws InterruptedException {
//            int threadCount = 200;
//            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//            CountDownLatch latch = new CountDownLatch(threadCount);
//
//            AtomicInteger successCount = new AtomicInteger(0);
//            AtomicInteger failCount = new AtomicInteger(0);
//
//            long startTime = System.currentTimeMillis();
//
//            for(int i=0; i<threadCount; i++){
//                final int index = i;
//                executor.submit(() -> {
//                    try {
//                        OrderRequest request = new OrderRequest(testGroupBuy.getId(), 1);
//                        orderJvmService.createOrder_Reen(testUsers.get(index).getId(), request);
//                        successCount.incrementAndGet();
//                    } catch (Exception e) {
//                        //System.err.println("!!!!!!주문실패!!!!!!!!!!!!!");
//                        failCount.incrementAndGet();
//                    } finally {
//                        latch.countDown();
//                    }
//                });
//            }
//
//            latch.await();
//            executor.shutdown();
//            executor.awaitTermination(1, TimeUnit.MINUTES);
//
//            long endTime = System.currentTimeMillis();
//
//            GroupBuy updatedGroupBuy = groupBuyRepository.findById(testGroupBuy.getId())
//                    .orElseThrow();
//
//            System.out.println("성공 주문 수: " + successCount.get());
//            System.out.println("실패 주문 수: " + failCount.get());
//            System.out.println("남은 수량: " + updatedGroupBuy.getRemainingQuantity());
//            System.out.println("걸린 시간: " + (endTime - startTime) + "ms");
//
//            assertEquals(10, successCount.get() + updatedGroupBuy.getRemainingQuantity());
//            assertTrue(updatedGroupBuy.getRemainingQuantity() >= 0);
//        }
//
//    }
//
//    @Nested
//    @DisplayName("개별 reentrantLock을 사용해 동시에 주문을 요청한다.")
//    class createOrderTest_reentrantLock_Map {
//
//        @Test
//        @DisplayName("개별 reen_동시에 100명이 공동구매 상품에 주문을 요청한다.")
//        public void createOrderTest_reen() throws InterruptedException {
//            int threadCount = 1000;
//            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//            CountDownLatch latch = new CountDownLatch(threadCount);
//
//            AtomicInteger successCount = new AtomicInteger(0);
//            AtomicInteger failCount = new AtomicInteger(0);
//
//            long startTime = System.currentTimeMillis();
//
//            for(int i=0; i<threadCount; i++){
//                final int index = i;
//                executor.submit(() -> {
//                    try {
//                        OrderRequest request = new OrderRequest(testGroupBuy.getId(), 1);
//                        orderJvmService.createOrder_Reen_Map(testUsers.get(index).getId(), request);
//                        successCount.incrementAndGet();
//                    } catch (Exception e) {
//                        //System.err.println("!!!!!!주문실패!!!!!!!!!!!!!");
//                        failCount.incrementAndGet();
//                    } finally {
//                        latch.countDown();
//                    }
//                });
//            }
//
//            latch.await();
//            executor.shutdown();
//            executor.awaitTermination(1, TimeUnit.MINUTES);
//
//            long endTime = System.currentTimeMillis();
//
//            GroupBuy updatedGroupBuy = groupBuyRepository.findById(testGroupBuy.getId())
//                    .orElseThrow();
//
//            System.out.println("성공 주문 수: " + successCount.get());
//            System.out.println("실패 주문 수: " + failCount.get());
//            System.out.println("남은 수량: " + updatedGroupBuy.getRemainingQuantity());
//            System.out.println("걸린 시간: " + (endTime - startTime) + "ms");
//
//            assertEquals(640, successCount.get() + updatedGroupBuy.getRemainingQuantity());
//            assertTrue(updatedGroupBuy.getRemainingQuantity() >= 0);
//        }
//
//
//        @Test
//        @DisplayName("개별 reen_동시에 200명이 공동구매 상품에 주문을 요청한다.")
//        public void createOrderTest2_reen() throws InterruptedException {
//            int threadCount = 200;
//            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
//            CountDownLatch latch = new CountDownLatch(threadCount);
//
//            AtomicInteger successCount = new AtomicInteger(0);
//            AtomicInteger failCount = new AtomicInteger(0);
//
//            long startTime = System.currentTimeMillis();
//
//            for(int i=0; i<threadCount; i++){
//                final int index = i;
//                executor.submit(() -> {
//                    try {
//                        OrderRequest request = new OrderRequest(testGroupBuy.getId(), 1);
//                        orderJvmService.createOrder_Reen_Map(testUsers.get(index).getId(), request);
//                        successCount.incrementAndGet();
//                    } catch (Exception e) {
//                        //System.err.println("!!!!!!주문실패!!!!!!!!!!!!!");
//                        failCount.incrementAndGet();
//                    } finally {
//                        latch.countDown();
//                    }
//                });
//            }
//
//            latch.await();
//            executor.shutdown();
//            executor.awaitTermination(1, TimeUnit.MINUTES);
//
//            long endTime = System.currentTimeMillis();
//
//            GroupBuy updatedGroupBuy = groupBuyRepository.findById(testGroupBuy.getId())
//                    .orElseThrow();
//
//            System.out.println("성공 주문 수: " + successCount.get());
//            System.out.println("실패 주문 수: " + failCount.get());
//            System.out.println("남은 수량: " + updatedGroupBuy.getRemainingQuantity());
//            System.out.println("걸린 시간: " + (endTime - startTime) + "ms");
//
//            assertEquals(10, successCount.get() + updatedGroupBuy.getRemainingQuantity());
//            assertTrue(updatedGroupBuy.getRemainingQuantity() >= 0);
//        }
//
//    }
//
//
//    @Test
//    @DisplayName("syn_동시에 200명이 공동구매 상품에 주문을 요청한다.")
//    public void createOrderTest_syn() throws InterruptedException {
//        int threadCount = 1000;
//        ExecutorService executor = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        AtomicInteger successCount = new AtomicInteger(0);
//        AtomicInteger failCount = new AtomicInteger(0);
//
//        long startTime = System.currentTimeMillis();
//
//        for(int i=0; i<threadCount; i++){
//            final int index = i;
//            executor.submit(() -> {
//                try {
//                    OrderRequest request = new OrderRequest(testGroupBuy.getId(), 1);
//                    orderJvmService.createOrder_syn(testUsers.get(index).getId(), request);
//                    successCount.incrementAndGet();
//                } catch (Exception e) {
//                    //System.err.println("!!!!!!주문실패!!!!!!!!!!!!!");
//                    failCount.incrementAndGet();
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
//        long endTime = System.currentTimeMillis();
//
//        GroupBuy updatedGroupBuy = groupBuyRepository.findById(testGroupBuy.getId())
//                .orElseThrow();
//
//        System.out.println("성공 주문 수: " + successCount.get());
//        System.out.println("실패 주문 수: " + failCount.get());
//        System.out.println("남은 수량: " + updatedGroupBuy.getRemainingQuantity());
//        System.out.println("걸린 시간: " + (endTime - startTime) + "ms");
//
//        assertEquals(640, successCount.get() + updatedGroupBuy.getRemainingQuantity());
//        assertTrue(updatedGroupBuy.getRemainingQuantity() >= 0);
//    }
//
//
//    @Test
//    @DisplayName("낙관적 락 충돌 테스트 - 동시 주문 시도")
//    public void optimisticLockingTest() throws InterruptedException {
//        int threadCount = 1000;
//        ExecutorService executor = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        AtomicInteger successCount = new AtomicInteger(0);
//        AtomicInteger failCount = new AtomicInteger(0);
//
//        long startTime = System.currentTimeMillis();
//
//        for(int i=0; i<threadCount; i++){
//            final int index = i;
//            executor.submit(() -> {
//                try {
//                    OrderRequest request = new OrderRequest(testGroupBuy.getId(), 1);
//                    orderConcurrencyService.createOrder_Opti2(testUsers.get(index).getId(), request);
//                    successCount.incrementAndGet();
//                } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
//                    System.out.println("락 충돌 발생! 재시도 필요");
//                    failCount.incrementAndGet();
//                } catch (Exception e) {
//                    failCount.incrementAndGet();
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
//        long endTime = System.currentTimeMillis();
//
//        GroupBuy updatedGroupBuy = groupBuyRepository.findById(testGroupBuy.getId())
//                .orElseThrow();
//
//        System.out.println("성공 주문 수: " + successCount.get());
//        System.out.println("실패 주문 수: " + failCount.get());
//        System.out.println("남은 수량: " + updatedGroupBuy.getRemainingQuantity());
//        System.out.println("걸린 시간: " + (endTime - startTime) + "ms");
//
//        assertEquals(640, successCount.get(), "성공 주문 수는 50건이어야 합니다.");
//        assertEquals(0, updatedGroupBuy.getRemainingQuantity(), "남은 재고는 0이어야 합니다.");
//        assertTrue(updatedGroupBuy.getRemainingQuantity() >= 0, "남은 수량은 음수일 수 없습니다");
//    }
//
//    @Test
//    @DisplayName("비관적 락 테스트 - 동시 주문 시도")
//    public void pessimisticLockingTest() throws InterruptedException {
//        int threadCount = 1000;
//        ExecutorService executor = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        AtomicInteger successCount = new AtomicInteger(0);
//        AtomicInteger failCount = new AtomicInteger(0);
//
//        long startTime = System.currentTimeMillis();
//
//        for (int i = 0; i < threadCount; i++) {
//            final int index = i;
//            executor.submit(() -> {
//                try {
//                    OrderRequest request = new OrderRequest(testGroupBuy.getId(), 1);
//                    orderConcurrencyService.createOrderWithPess(testUsers.get(index).getId(), request); // 👈 비관적 락 버전 호출
//                    successCount.incrementAndGet();
//                } catch (Exception e) {
//                    failCount.incrementAndGet();
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
//        long endTime = System.currentTimeMillis();
//
//        GroupBuy updatedGroupBuy = groupBuyRepository.findById(testGroupBuy.getId())
//                .orElseThrow();
//
//        System.out.println("성공 주문 수: " + successCount.get());
//        System.out.println("실패 주문 수: " + failCount.get());
//        System.out.println("남은 수량: " + updatedGroupBuy.getRemainingQuantity());
//        System.out.println("걸린 시간: " + (endTime - startTime) + "ms");
//
//        assertEquals(640, successCount.get() + updatedGroupBuy.getRemainingQuantity());
//        assertTrue(updatedGroupBuy.getRemainingQuantity() >= 0);
//    }
//
//    @Test
//    @DisplayName("Redisson 분산 락 테스트 - 동시 주문 시도")
//    public void redissonLockingTest() throws InterruptedException {
//        int threadCount = 1000;
//        ExecutorService executor = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        AtomicInteger successCount = new AtomicInteger(0);
//        AtomicInteger failCount = new AtomicInteger(0);
//
//        long startTime = System.currentTimeMillis();
//
//        for (int i = 0; i < threadCount; i++) {
//            final int index = i;
//            executor.submit(() -> {
//                try {
//                    OrderRequest request = new OrderRequest(testGroupBuy.getId(), 1);
//                    orderConcurrencyService.createOrderWithRedisson(testUsers.get(index).getId(), request); // 레디슨 락 적용 메서드 호출
//                    successCount.incrementAndGet();
//                } catch (Exception e) {
//                    failCount.incrementAndGet();
//                    System.err.println("주문 실패: " + e.getMessage());
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//        executor.shutdown();
//        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
//            executor.shutdownNow();
//            System.err.println("스레드풀 강제 종료됨");
//        }
//
//        long endTime = System.currentTimeMillis();
//
//        GroupBuy updatedGroupBuy = groupBuyRepository.findById(testGroupBuy.getId())
//                .orElseThrow(() -> new RuntimeException("GroupBuy 데이터 없음"));
//
//        System.out.println("성공 주문 수: " + successCount.get());
//        System.out.println("실패 주문 수: " + failCount.get());
//        System.out.println("남은 수량: " + updatedGroupBuy.getRemainingQuantity());
//        System.out.println("걸린 시간: " + (endTime - startTime) + "ms");
//
//        assertEquals(640, successCount.get(), "성공 주문 수는 50건이어야 합니다.");
//        assertEquals(0, updatedGroupBuy.getRemainingQuantity(), "남은 재고는 0이어야 합니다.");
//        assertTrue(updatedGroupBuy.getRemainingQuantity() >= 0, "남은 수량은 음수일 수 없습니다");
//    }
//
//    @Test
//    @DisplayName("Redis-lau-script 테스트 - 동시 주문 시도")
//    public void redisLockingTest() throws InterruptedException {
//        int threadCount = 10;
//        ExecutorService executor = Executors.newFixedThreadPool(5);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        AtomicInteger successCount = new AtomicInteger(0);
//        AtomicInteger failCount = new AtomicInteger(0);
//
//        long startTime = System.currentTimeMillis();
//
//        for (int i = 0; i < threadCount; i++) {
//            final int index = i;
//            executor.submit(() -> {
//                try {
//                    OrderRequest request = new OrderRequest(testGroupBuy.getId(), 1);
//                    System.out.println("thread: " + index + ", groupBuyId: " + testGroupBuy.getId());
//                    orderLauScriptService.tryCreateOrderWithRedisWithKafka(testUsers.get(index).getId(), request); // 레디슨 락 적용 메서드 호출
//                    successCount.incrementAndGet();
//                } catch (Exception e) {
//                    failCount.incrementAndGet();
//                    System.err.println("주문 실패: " + e.getMessage());
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//        executor.shutdown();
//        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
//            executor.shutdownNow();
//            System.err.println("스레드풀 강제 종료됨");
//        }
//
//        long endTime = System.currentTimeMillis();
//
//        await()
//                .atMost(30, SECONDS)
//                .pollInterval(1, SECONDS)
//                .untilAsserted(() -> {
//                    // 남은 수량이 0이 될 때까지 대기
//                    GroupBuy updatedGroupBuy = groupBuyRepository.findById(testGroupBuy.getId())
//                            .orElseThrow(() -> new RuntimeException("GroupBuy 데이터 없음"));
//
//                    assertEquals(0, updatedGroupBuy.getRemainingQuantity(), "재고가 모두 소진되어야 합니다");
//
//                });
//
//        System.out.println("성공 주문 수: " + successCount.get());
//        System.out.println("실패 주문 수: " + failCount.get());
//        System.out.println("걸린 시간: " + (endTime - startTime) + "ms");
//
//        assertEquals(5, successCount.get(), "성공 주문 수는 50건이어야 합니다.");
//    }
//
//
//
//}
