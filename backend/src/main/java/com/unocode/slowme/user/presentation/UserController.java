package com.unocode.slowme.user.presentation;

import com.unocode.slowme.common.error.code.ErrorCode;
import com.unocode.slowme.common.error.exception.SlowmeException;
import com.unocode.slowme.user.application.FollowService;
import com.unocode.slowme.user.infra.UserEntity;
import com.unocode.slowme.user.infra.UserJpaRepository;
import com.unocode.slowme.user.presentation.dto.CountResponse;
import com.unocode.slowme.user.presentation.dto.CreateUserRequest;
import com.unocode.slowme.user.presentation.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	private final UserJpaRepository userRepo;
	private final FollowService followService;

	@PostMapping
	public UserResponse create(@Valid @RequestBody CreateUserRequest req) {
		UserEntity saved = userRepo.save(new UserEntity(req.nickname()));
		return new UserResponse(saved.getId(), saved.getNickname());
	}

	@GetMapping("/{userId}")
	public UserResponse get(@PathVariable Long userId) {
		UserEntity user = userRepo.findById(userId)
				.orElseThrow(() -> new SlowmeException(ErrorCode.NOT_FOUND, "유저를 찾을 수 없습니다. userId=" + userId));
		return new UserResponse(user.getId(), user.getNickname());
	}

	@PostMapping("/{userId}/follow/{targetUserId}")
	public ResponseEntity<Void> follow(@PathVariable Long userId, @PathVariable Long targetUserId) {
		followService.follow(userId, targetUserId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{userId}/follow/{targetUserId}")
	public ResponseEntity<Void> unfollow(@PathVariable Long userId, @PathVariable Long targetUserId) {
		followService.unfollow(userId, targetUserId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{userId}/followers/count")
	public CountResponse followersCount(@PathVariable Long userId) {
		return new CountResponse(followService.followersCount(userId));
	}

	@GetMapping("/{userId}/following/count")
	public CountResponse followingCount(@PathVariable Long userId) {
		return new CountResponse(followService.followingCount(userId));
	}
}


