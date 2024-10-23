package com.bremen.backend.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bremen.backend.domain.user.dto.UserLoginRequest;
import com.bremen.backend.domain.user.dto.UserLoginResponse;
import com.bremen.backend.domain.user.dto.UserReissueResponse;
import com.bremen.backend.domain.user.service.AuthService;
import com.bremen.backend.domain.user.service.ReissueService;
import com.bremen.backend.global.response.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Auth", description = "유저 인증 API")
public class AuthController {
	private final AuthService authService;
	private final ReissueService reissueService;

	@PostMapping("/login")
	@Operation(summary = "로그인을 합니다.")
	public ResponseEntity<Response<UserLoginResponse>> login(@RequestBody UserLoginRequest userLoginRequest) {
		UserLoginResponse userLoginResponse = authService.login(userLoginRequest);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "로그인 성공", userLoginResponse));
	}

	@GetMapping("/logout")
	@Operation(summary = "로그아웃을 합니다.")
	public ResponseEntity<Response<String>> logout(@RequestHeader("Authorization") String accessToken,
		@RequestHeader("Refresh-Token") String refreshToken) {
		authService.logout(accessToken, refreshToken);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "logout 성공!", ""));
	}

	@GetMapping("/reissue")
	@Operation(summary = "토큰을 재발급합니다.", description = "액세스 토큰이 만료된 경우 리프레쉬 토큰을 이용하여 토큰을 재발급합니다.")
	public ResponseEntity<Response<UserReissueResponse>> tokenReissue(
		@RequestHeader("Refresh-Token") String refreshToken) {
		UserReissueResponse userReissueResponse = reissueService.reissueAccessToken(refreshToken);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "새로운 액세스 토큰 발급 성공!", userReissueResponse));
	}
}
