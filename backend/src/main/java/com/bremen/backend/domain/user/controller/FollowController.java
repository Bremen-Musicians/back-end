package com.bremen.backend.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bremen.backend.domain.user.service.FollowUserService;
import com.bremen.backend.global.response.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Follow", description = "팔로우 API")
public class FollowController {
	private final FollowUserService followUserService;

	@GetMapping("/follow")
	@Operation(summary = "해당 ID를 가진 사람을 팔로우합니다.")
	public ResponseEntity<Response<String>> follow(@RequestParam(value = "nickname") String nickname) {
		if (followUserService.followUser(nickname)) {
			return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "팔로우 성공", ""));
		} else {
			return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "언팔로우 성공", ""));
		}

	}
}
