package com.secure_tikkle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.secure_tikkle.domain.Badge;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
	
	Optional<Badge> findByCode(String code);
	
	@org.springframework.data.jpa.repository.Query("""
		    select new com.secure_tikkle.dto.BadgeDto(
		      b.code, b.title, b.description, b.icon,
		      (ub.id is not null),
		      ub.unlockedAt
		    )
		    from Badge b
		    left join UserBadge ub
		      on ub.badge = b
		     and ub.user.id = :uid
		    order by b.id
		  """)
		  java.util.List<com.secure_tikkle.dto.BadgeDto> findAllWithEarned(@org.springframework.data.repository.query.Param("uid") Long uid);
	

}
