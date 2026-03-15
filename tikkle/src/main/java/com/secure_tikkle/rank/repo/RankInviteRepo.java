package com.secure_tikkle.rank.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.secure_tikkle.domain.RankInvite;

public interface RankInviteRepo extends JpaRepository<RankInvite, Long> {

	Optional<RankInvite> findByCode(String code);
    List<RankInvite> findByGroupId(Long groupId);
}
