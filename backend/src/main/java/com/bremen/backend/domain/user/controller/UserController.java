package com.bremen.backend.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bremen.backend.domain.user.dto.UserRegistrationRequest;
import com.bremen.backend.domain.user.dto.UserResponse;
import com.bremen.backend.domain.user.service.RegistrationService;
import com.bremen.backend.domain.user.service.UserService;
import com.bremen.backend.global.response.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
@Tag(name = "User", description = "유저 API")
public class UserController {

	private final UserService userService;
	private final RegistrationService registrationService;

	@GetMapping()
	@Operation(summary = "유저 닉네임으로 유저 정보를 조회합니다.", description = "유저의 닉네임값을 파라미터로 받습니다.")
	ResponseEntity<Response<UserResponse>> userDetails(@RequestParam("nickname") String nickname) {
		UserResponse userResponse = userService.findUserByNickname(nickname);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "조회 성공", userResponse));
	}

	@PostMapping()
	@Operation(summary = "회원가입을 합니다", description = "유저 정보를 입력받아 신규 유저를 추가합니다.")
	ResponseEntity<Response<UserResponse>> userAdd(@RequestBody UserRegistrationRequest userRegistrationRequest) {
		UserResponse userResponse = registrationService.register(userRegistrationRequest);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "유저가 성공적으로 추가되었습니다", userResponse));
	}

	@DeleteMapping()
	@Operation(summary = "해당 회원을 삭제합니다", description = "유저의 id값으로 구분하여 해당 회원을 삭제하자")
	ResponseEntity<Response<Long>> userRemove(@RequestParam("id") Long id) {
		Long memberId = userService.removeUser(id);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "유저가 성공적으로 삭제되었습니다", memberId));
	}

	@GetMapping("/check-username")
	@Operation(summary = "중복된 이메일을 체크합니다.")
	public ResponseEntity<Response<String>> duplicateCheckEmail(
		@RequestParam(value = "username") String username) {
		if (username != null) {
			userService.duplicateUsername(username);
			return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "이메일 중복체크 성공", ""));
		} else {
			return ResponseEntity.ok(new Response(HttpStatus.NO_CONTENT.value(), "파라미터는 비어있을 수 없습니다.", ""));
		}
	}

	@GetMapping("/check-nickname")
	@Operation(summary = "중복된 닉네임을 체크합니다.")
	public ResponseEntity<Response<String>> duplicateCheckNickname(
		@RequestParam(value = "nickname") String nickname) {
		if (nickname != null) {
			userService.duplicateNickname(nickname);
			return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "닉네임 중복체크 성공", ""));
		} else {
			return ResponseEntity.ok(new Response(HttpStatus.NO_CONTENT.value(), "파라미터는 비어있을 수 없습니다.", ""));
		}
	}

}
