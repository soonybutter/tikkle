package com.secure_tikkle.rank.repo;

import com.secure_tikkle.domain.RankGroup;

import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface RankGroupRepo extends JpaRepository<RankGroup, Long> {
	
	List<RankGroup> findByOwnerId(Long ownerId);
	
	
}