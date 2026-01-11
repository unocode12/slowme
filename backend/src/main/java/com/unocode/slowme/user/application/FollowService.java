package com.unocode.slowme.user.application;

import com.unocode.slowme.common.error.code.ErrorCode;
import com.unocode.slowme.common.error.exception.SlowmeException;
import com.unocode.slowme.user.infra.FollowEntity;
import com.unocode.slowme.user.infra.FollowJpaRepository;
import com.unocode.slowme.user.infra.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final FollowJpaRepository followRepo;
	private final UserJpaRepository userRepo;

	@Transactional
	public void follow(Long followerId, Long followeeId) {
		validateIds(followerId, followeeId);
		ensureUserExists(followerId);
		ensureUserExists(followeeId);

		// idempotent fast-path (avoid unique constraint exception at commit time)
		if (followRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
			return;
		}

		try {
			followRepo.save(new FollowEntity(followerId, followeeId));
		} catch (DataIntegrityViolationException e) {
			// unique(followerId, followeeId) hit => already followed (idempotent)
		}
	}

	@Transactional
	public void unfollow(Long followerId, Long followeeId) {
		validateIds(followerId, followeeId);
		ensureUserExists(followerId);
		ensureUserExists(followeeId);

		// idempotent: if not followed, do nothing
		followRepo.deleteByFollowerIdAndFolloweeId(followerId, followeeId);
	}

	@Transactional(readOnly = true)
	public long followersCount(Long userId) {
		ensureUserExists(userId);
		return followRepo.countByFolloweeId(userId);
	}

	@Transactional(readOnly = true)
	public long followingCount(Long userId) {
		ensureUserExists(userId);
		return followRepo.countByFollowerId(userId);
	}

	private static void validateIds(Long followerId, Long followeeId) {
		if (followerId == null || followeeId == null) {
			throw new SlowmeException(ErrorCode.INVALID_REQUEST, "userId는 필수입니다.");
		}
		if (followerId.equals(followeeId)) {
			throw new SlowmeException(ErrorCode.INVALID_REQUEST, "자기 자신을 팔로우할 수 없습니다.");
		}
	}

	private void ensureUserExists(Long userId) {
		if (!userRepo.existsById(userId)) {
			throw new SlowmeException(ErrorCode.NOT_FOUND, "유저를 찾을 수 없습니다. userId=" + userId);
		}
	}
}


