package com.bremen.backend.domain.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bremen.backend.domain.notification.NotificationDto;
import com.bremen.backend.domain.notification.entity.Notification;
import com.bremen.backend.domain.notification.mapper.NotificationMapper;
import com.bremen.backend.domain.notification.repository.NotificationQueryDslRepository;
import com.bremen.backend.domain.notification.repository.NotificationRepository;
import com.bremen.backend.domain.user.entity.PrincipalDetails;
import com.bremen.backend.domain.user.entity.User;
import com.bremen.backend.global.CustomException;
import com.bremen.backend.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
	private final NotificationRepository notificationRepository;
	private final NotificationQueryDslRepository notificationQueryDslRepository;

	private final RedisTemplate<String, Object> redisTemplate;
	private final static long CACHE_TTL = 60 * 5; // 알림의 TTL

	@Override
	@Transactional
	public void addNotification(PrincipalDetails principalDetails, NotificationDto notificationDto) {
		Notification notification = NotificationMapper.INSTANCE.dtoToEntity(notificationDto);
		notification.addUser(principalDetails.getUser());
		notificationRepository.save(notification);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<NotificationDto> getNotification(PrincipalDetails principalDetails, Pageable pageable) {
		// 사용자 토큰을 이용해 username을 가져옴
		String username = principalDetails.getUsername();
		String cacheKey = "notifications:" + username + ":page:" + pageable.getPageNumber();

		Map<String, Object> cachedData = (Map<String, Object>)redisTemplate.opsForValue().get(cacheKey);

		if (cachedData != null) {
			List<NotificationDto> content = (List<NotificationDto>)cachedData.get("content");
			long totalElements = (Long)cachedData.get("totalElements");

			return new PageImpl<>(content, pageable, totalElements);
		}
		//캐시된 데이터가 있을 경우

		Page<Notification> pages = notificationRepository.findByUser(username, pageable);
		Page<NotificationDto> notificationDtos = pages.map(NotificationMapper.INSTANCE::entityToDto);

		Map<String, Object> cacheData = new HashMap<>();
		cacheData.put("content", notificationDtos.getContent());
		cacheData.put("totalElements", pages.getTotalElements());

		redisTemplate.opsForValue().set(cacheKey, cacheData, CACHE_TTL, TimeUnit.SECONDS);

		return notificationDtos;
	}

	@Override
	@Transactional
	public Long deleteNotification(PrincipalDetails principalDetails, ArrayList<Long> ids) {
		if (ids.isEmpty()) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
		User user = principalDetails.getUser();
		Long updateCount = notificationQueryDslRepository.updateColumnForIds(ids, user.getId());

		deleteCache(user);

		return updateCount;
	}

	private void deleteCache(User user) {
		// 모든 페이지 캐시 삭제
		String cacheKeyPattern = "notifications:" + user.getUsername() + ":page:*";
		Set<String> keys = redisTemplate.keys(cacheKeyPattern);
		if (keys != null && !keys.isEmpty()) {
			redisTemplate.delete(keys);
		}
	}

}
