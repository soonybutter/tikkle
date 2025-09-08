package com.secure_tikkle.rank.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.secure_tikkle.domain.RankGroupMember;

public interface RankGroupMemberRepo extends JpaRepository<RankGroupMember, Long> {

	boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    List<RankGroupMember> findByUserId(Long userId);
    List<RankGroupMember> findByGroupId(Long groupId);

    void deleteByGroupIdAndUserId(Long groupId, Long userId);
    
}
