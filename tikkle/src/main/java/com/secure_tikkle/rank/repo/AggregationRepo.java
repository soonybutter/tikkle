package com.secure_tikkle.rank.repo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional(readOnly = true)
public class AggregationRepo {

	@PersistenceContext
	  private EntityManager em;

	  public static final class LeaderRow {
	    public final Long userId;
	    public final String name;
	    public final Long total;
	    public final Long last30d;
	    public LeaderRow(Long userId, String name, Long total, Long last30d) {
	      this.userId = userId; this.name = name; this.total = total; this.last30d = last30d;
	    }
	  }

	  public List<LeaderRow> findGroupLeaders(Long groupId, LocalDateTime d30) {
	    String sql = """
	        SELECT u.id AS userId,
	               u.name AS name,
	               COALESCE(SUM(sl.amount), 0) AS total,
	               COALESCE(SUM(CASE WHEN sl.created_at >= :d30 THEN sl.amount ELSE 0 END), 0) AS last30d
	        FROM rank_group_member gm
	        JOIN users u ON u.id = gm.user_id
	        LEFT JOIN goal g ON g.user_id = u.id
	        LEFT JOIN savings_log sl ON sl.goal_id = g.id
	        WHERE gm.group_id = :gid
	        GROUP BY u.id, u.name
	        ORDER BY total DESC
	        """;
	    var list = em.createNativeQuery(sql)
	        .setParameter("gid", groupId)
	        .setParameter("d30", Timestamp.valueOf(d30))
	        .getResultList();

	    var out = new ArrayList<LeaderRow>();
	    for (Object row : list) {
	      Object[] a = (Object[]) row;
	      out.add(new LeaderRow(
	          ((Number) a[0]).longValue(),
	          (String) a[1],
	          ((Number) a[2]).longValue(),
	          ((Number) a[3]).longValue()
	      ));
	    }
	    return out;
	  }
}
