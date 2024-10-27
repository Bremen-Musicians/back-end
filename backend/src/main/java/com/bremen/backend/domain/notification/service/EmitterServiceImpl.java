package com.bremen.backend.domain.notification.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bremen.backend.domain.notification.entity.NotificationType;
import com.bremen.backend.domain.notification.repository.EmitterRepository;
import com.bremen.backend.domain.user.entity.PrincipalDetails;
import com.bremen.backend.global.CustomException;
import com.bremen.backend.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmitterServiceImpl implements EmitterService {
	private final EmitterRepository emitterRepository;
	private final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
	private final String ALARM_NAME = "alarm";

	@Override
	public void send(final Long alarmId, final String username, final String msg, NotificationType type) {
		emitterRepository.get(username).ifPresentOrElse(sseEmitter -> {
			try {
				sseEmitter.send(SseEmitter.event().id(createAlarmId(username, alarmId)).name(ALARM_NAME).data(msg));
			} catch (IOException e) {
				emitterRepository.delete(username);
				throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
			}
		}, () -> log.info("[SseEmitter] {} SseEmitter Not Founded", username));
	}

	@Override
	public SseEmitter connectAlarm(PrincipalDetails principalDetails) {
		SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
		String username = principalDetails.getUsername();
		emitterRepository.save(username, sseEmitter);

		// 종료 되었을 때 처리
		sseEmitter.onCompletion(() -> {
			emitterRepository.delete(username);
		});

		// timeOut 시 처리
		sseEmitter.onTimeout(() -> {
			emitterRepository.delete(username);
		});

		try {
			sseEmitter.send(
				SseEmitter.event().id(createAlarmId(username, null)).name(ALARM_NAME).data("connect completed!!"));
		} catch (IOException e) {
			throw new CustomException(ErrorCode.ALARM_CONNECT_ERROR);
		}

		return sseEmitter;
	}

	@Override
	public String createAlarmId(String username, Long alarmId) {
		if (alarmId == null) {
			return username + "_" + System.currentTimeMillis();
		}
		return username + "_" + alarmId;
	}
}
