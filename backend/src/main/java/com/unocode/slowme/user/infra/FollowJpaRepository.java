package com.unocode.slowme.user.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowJpaRepository extends JpaRepository<FollowEntity, Long> {
	boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

	long countByFolloweeId(Long followeeId);

	long countByFollowerId(Long followerId);

	@Modifying
	@Query("""
			delete from FollowEntity f
			where f.followerId = :followerId
			  and f.followeeId = :followeeId
			""")
	int deleteByFollowerIdAndFolloweeId(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);
}


