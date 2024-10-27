package com.bremen.backend.domain.notification.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bremen.backend.domain.notification.entity.NotificationType;
import com.bremen.backend.domain.user.entity.PrincipalDetails;

public interface EmitterService {
	void send(Long alarmId, String username, String msg, NotificationType type);

	SseEmitter connectAlarm(PrincipalDetails principalDetails);

	String createAlarmId(String username, Long alarmId);
}
