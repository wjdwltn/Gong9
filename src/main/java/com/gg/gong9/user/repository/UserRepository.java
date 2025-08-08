package com.gg.gong9.user.repository;

import com.gg.gong9.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //이메일로 사용자 조회
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    //회원가입 시 이메일 중복 확인
    boolean existsByEmail(String email);

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    @Query("select distinct o.user from Order o where o.groupBuy.id = :groupBuyId and o.isDeleted = false")
    List<User> findDistinctUsersByOrdersGroupBuyId(@Param("groupBuyId") Long groupBuyId);
}
