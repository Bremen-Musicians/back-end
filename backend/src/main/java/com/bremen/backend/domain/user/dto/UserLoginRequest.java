package com.bremen.backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {
	@NotBlank(message = "아이디는 비어있을 수 없습니다.")
	private String username;
	@NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
	private String password;
}
