package com.bremen.backend.domain.user.service;

import static com.bremen.backend.domain.notification.entity.NotificationType.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bremen.backend.domain.notification.NotificationDto;
import com.bremen.backend.domain.notification.entity.NotificationType;
import com.bremen.backend.domain.notification.service.EmitterService;
import com.bremen.backend.domain.notification.service.NotificationService;
import com.bremen.backend.domain.user.entity.PrincipalDetails;
import com.bremen.backend.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowUserServiceImpl implements FollowUserService {
	private final FollowService followService;
	private final EmitterService emitterService;
	private final NotificationService notificationService;
	private final UserService userService;

	@Override
	@Transactional
	public boolean followUser(PrincipalDetails principalDetails, String nickname) {
		User follower = principalDetails.getUser(); // 토큰으로 현재 로그인한 유저 조회
		User follow = userService.getUserByNickname(nickname);

		boolean isExist = followService.isFollower(follow, follower);
		String message = follower.getNickname();

		if (isExist) {
			// 이미 팔로우 하고있다면
			followService.unfollow(follow, follower);
			message += "님이 언팔로우 했습니다.";
		} else {
			followService.follow(follow, follower);
			message += "님이 팔로우 했습니다.";
		}
		String username = follow.getUsername();
		NotificationType type = FOLLOW;
		NotificationDto notificationDto = NotificationDto.builder()
			.content(message)
			.type(type)
			.build();
		notificationService.addNotification(notificationDto, username);
		emitterService.send(null, username, message, type);
		return !isExist;

	}
}
