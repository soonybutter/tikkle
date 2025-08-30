package com.secure_tikkle.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
  name = "users",
  uniqueConstraints = {
    @UniqueConstraint(name="uq_users_provider_userkey", columnNames={"provider","user_key"})
  }
)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=255)                 // null 허용
    private String email;

    @Column(nullable=false, length=100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Provider provider;          // GOOGLE / NAVER / KAKAO

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Role role;

    @Column(name="user_key", nullable=false, length=100)  // 공급자별 고유 ID
    private String userKey;
}