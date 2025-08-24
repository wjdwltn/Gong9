package com.gg.gong9.participation.repository;

import com.gg.gong9.minibuy.entity.MiniBuy;
import com.gg.gong9.participation.entity.Participation;

import com.gg.gong9.participation.entity.ParticipationStatus;
import com.gg.gong9.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findAllByUserId(Long userId);

    boolean existsByUserAndMiniBuy(User user, MiniBuy miniBuy);

    List<Participation> findByMiniBuyId(Long miniBuyId);

    boolean existsByUserAndMiniBuyIdAndStatus(User user, Long miniBuyId, ParticipationStatus status);

    @Query("SELECT p.user FROM Participation p " +
            "WHERE p.miniBuy.id = :miniBuyId " +
            "AND p.status = com.gg.gong9.participation.entity.ParticipationStatus.JOINED")
    List<User> findAllUsersByMiniBuyId(@Param("miniBuyId") Long miniBuyId);

}
