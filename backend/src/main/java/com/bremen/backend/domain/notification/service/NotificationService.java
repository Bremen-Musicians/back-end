package com.bremen.backend.domain.notification.service;

import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bremen.backend.domain.notification.NotificationDto;
import com.bremen.backend.domain.user.entity.PrincipalDetails;

public interface NotificationService {
	void addNotification(PrincipalDetails principalDetails, NotificationDto notification);

	Page<NotificationDto> getNotification(PrincipalDetails principalDetails, Pageable pageable);

	Long deleteNotification(PrincipalDetails principalDetails, ArrayList<Long> ids);

}
