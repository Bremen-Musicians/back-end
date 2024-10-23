package com.bremen.backend.domain.notification.service;

import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bremen.backend.domain.notification.NotificationDto;
import com.bremen.backend.domain.notification.entity.Notification;
import com.bremen.backend.domain.notification.mapper.NotificationMapper;
import com.bremen.backend.domain.notification.repository.NotificationQueryDslRepository;
import com.bremen.backend.domain.notification.repository.NotificationRepository;
import com.bremen.backend.domain.user.entity.User;
import com.bremen.backend.domain.user.service.UserService;
import com.bremen.backend.global.CustomException;
import com.bremen.backend.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
	private final NotificationRepository notificationRepository;
	private final UserService userService;
	private final NotificationQueryDslRepository notificationQueryDslRepository;

	@Override
	@Transactional
	public void addNotification(NotificationDto notificationDto, String username) {
		Notification notification = NotificationMapper.INSTANCE.dtoToEntity(notificationDto);
		notification.addUser(userService.getUserByUsername(username));
		notificationRepository.save(notification);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<NotificationDto> getNotification(Pageable pageable) {
		// 사용자 토큰을 이용해 username을 가져옴
		String username = userService.getUserByToken().getUsername();

		// 페이징된 Notification 엔티티 목록을 가져옴
		Page<Notification> pages = notificationRepository.findByUser(username, pageable);

		// Page<Notification>을 Page<NotificationDto>로 변환하여 반환
		return pages.map(NotificationMapper.INSTANCE::entityToDto);
	}

	public Notification getNotification(Long id) {
		return notificationRepository.findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.NOT_FOUND_NOTIFICATION)
		);
	}

	@Override
	@Transactional
	public Long deleteNotification(ArrayList<Long> ids) {
		if(ids.isEmpty()){
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
		User user = userService.getUserByToken();
		Long updateCount = notificationQueryDslRepository.updateColumnForIds(ids,user.getId());
		return updateCount;
	}

}
