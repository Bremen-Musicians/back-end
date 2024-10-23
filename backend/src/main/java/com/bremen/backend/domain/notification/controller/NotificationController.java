package com.bremen.backend.domain.notification.controller;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bremen.backend.domain.notification.NotificationDto;
import com.bremen.backend.domain.notification.service.EmitterService;
import com.bremen.backend.domain.notification.service.NotificationService;
import com.bremen.backend.global.response.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@Tag(name = "Notification", description = "알림 API")
public class NotificationController {
	private final EmitterService emitterService;
	private final NotificationService notificationService;

	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@Operation(summary = "알림 API를 연결합니다")
	SseEmitter subscribe(Authentication authentication, HttpServletResponse response) {
		response.setHeader("X-Accel-Buffering", "no");
		return emitterService.connectAlarm(authentication.getPrincipal().toString());
	}

	@GetMapping()
	@Operation(summary = "해당 유저에게 온 알림 메세지를 조회합니다.")
	ResponseEntity<Response<Page>> getNotification(Pageable pageable) {
		Page<NotificationDto> notificationDtos = notificationService.getNotification(pageable);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "알림 조회 성공",notificationDtos));
	}

	@DeleteMapping()
	@Operation(summary = "아이디를 기준으로 알림메세지를 삭제합니다.")
	ResponseEntity<Response<Long>> deleteNotification(@RequestParam ArrayList<Long> ids) {
		Long updateCount = notificationService.deleteNotification(ids);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "알림 메세지 삭제완료", updateCount));
	}
}
