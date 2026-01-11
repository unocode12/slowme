package com.unocode.slowme.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
		@NotBlank(message = "nickname은 필수입니다.")
		@Size(max = 50, message = "nickname은 50자 이하여야 합니다.")
		String nickname
) {
}


