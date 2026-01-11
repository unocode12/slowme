package com.unocode.slowme.user.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "follows",
		indexes = {
				@Index(name = "idx_follows_follower_id", columnList = "followerId"),
				@Index(name = "idx_follows_followee_id", columnList = "followeeId")
		},
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_follows_follower_followee", columnNames = {"followerId", "followeeId"})
		}
)
public class FollowEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long followerId;

	@Column(nullable = false)
	private Long followeeId;

	protected FollowEntity() {
	}

	public FollowEntity(Long followerId, Long followeeId) {
		this.followerId = followerId;
		this.followeeId = followeeId;
	}

	public Long getId() {
		return id;
	}

	public Long getFollowerId() {
		return followerId;
	}

	public Long getFolloweeId() {
		return followeeId;
	}
}


