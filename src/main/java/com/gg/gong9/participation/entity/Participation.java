package com.gg.gong9.participation.entity;

import com.gg.gong9.global.base.BaseEntity;
import com.gg.gong9.global.exception.exceptions.participation.ParticipationException;
import com.gg.gong9.global.exception.exceptions.participation.ParticipationExceptionMessage;
import com.gg.gong9.minibuy.entity.MiniBuy;
import com.gg.gong9.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "participation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "participation_id")
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mini_buy_id")
    private MiniBuy miniBuy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Participation(MiniBuy miniBuy, User user, ParticipationStatus status) {
        this.miniBuy = miniBuy;
        this.user = user;
        this.status = status;
    }

    public static Participation create(User user, MiniBuy miniBuy) {
        return new Participation(
                miniBuy,
                user,
                ParticipationStatus.JOINED
        );
    }

    public boolean isCanceled() {
        return this.status == ParticipationStatus.CANCELED;
    }

    public void cancel(){
        this.status = ParticipationStatus.CANCELED;
    }

    public void validateOwner(User user) {
        if(!this.user.getId().equals(user.getId())){
            throw new ParticipationException(ParticipationExceptionMessage.NOT_PERMISSION_PARTICIPATION);
        }
    }

}
