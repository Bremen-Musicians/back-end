package com.bremen.backend.domain.notification.service;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bremen.backend.domain.notification.NotificationDto;

public interface NotificationService {
	void addNotification(NotificationDto notification, String username);

	Page<NotificationDto> getNotification(Pageable pageable);

	Long deleteNotification(ArrayList<Long> ids);
	
}
