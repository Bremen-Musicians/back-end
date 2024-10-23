package com.bremen.backend.domain.user.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bremen.backend.domain.user.dto.UserProfileRequest;
import com.bremen.backend.domain.user.dto.UserProfileUpdateRequest;
import com.bremen.backend.domain.user.dto.UserProfileUpdateResponse;
import com.bremen.backend.domain.user.service.ProfileService;
import com.bremen.backend.global.response.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Profile", description = "유저 프로필 API")
public class ProfileController {
	private final ProfileService profileService;

	@PostMapping("/profile")
	@Operation(summary = "해당 회원의 추가 프로필 정보를 입력합니다.", description = "회원가입 과정에서 추가 프로필 정보를 입력하는데에 사용되는 API 입니다.")
	ResponseEntity<Response<Void>> userProfileAdd(UserProfileRequest userProfileRequest
	) throws IOException {
		profileService.modifyUserProfile(userProfileRequest);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "회원가입이 성공적으로 완료되었습니다.", null));
	}

	@PatchMapping("/profile")
	@Operation(summary = "현재 로그인한 회원의 프로필 정보를 수정합니다.")
	ResponseEntity<Response<Object>> userProfileModify(UserProfileUpdateRequest json) throws IOException {
		UserProfileUpdateResponse response = profileService.modifyUserProfile(json);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "프로필이 성공적으로 수정되었습니다.", response));
	}

}
