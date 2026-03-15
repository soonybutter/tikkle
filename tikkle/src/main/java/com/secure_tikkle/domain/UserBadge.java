package com.secure_tikkle.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity 
@Getter 
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
@Table(name="user_badge", uniqueConstraints = {
   @UniqueConstraint(name="uq_user_badge", columnNames = {"user_id","badge_id"})
})

public class UserBadge {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="badge_id", nullable=false)
    private Badge badge;

    private java.time.LocalDateTime unlockedAt;
}
