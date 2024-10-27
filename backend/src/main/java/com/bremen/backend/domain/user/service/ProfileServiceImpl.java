package com.bremen.backend.domain.user.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bremen.backend.domain.user.dto.UserProfileRequest;
import com.bremen.backend.domain.user.dto.UserProfileUpdateRequest;
import com.bremen.backend.domain.user.dto.UserProfileUpdateResponse;
import com.bremen.backend.domain.user.entity.PrincipalDetails;
import com.bremen.backend.domain.user.entity.User;
import com.bremen.backend.domain.user.mapper.UserMapper;
import com.bremen.backend.global.infra.s3.service.S3Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
	private final S3Service s3Service;

	@Override
	@Transactional
	public void modifyUserProfile(PrincipalDetails principalDetails, UserProfileRequest userProfileRequest) throws
		IOException {
		User user = principalDetails.getUser();
		if (userProfileRequest.getProfileImage().isEmpty()) {
			//이미지를 첨부하지 않고 랜덤이미지를 사용할경우
			user.modifyUserProfile(userProfileRequest.getProfileUrl(), user.getIntroduce());
		} else {
			String url = s3Service.streamUpload("profile", userProfileRequest.getProfileImage());
			user.modifyUserProfile(url, userProfileRequest.getIntroduce());
		}
	}

	@Override
	@Transactional
	public UserProfileUpdateResponse modifyUserProfile(PrincipalDetails principalDetails,
		UserProfileUpdateRequest userProfileUpdateRequest) throws
		IOException {
		User user = principalDetails.getUser(); // 현재 유저 정보 가져오기

		// 프로필 이미지가 있다면 업데이트, 없다면 기존 이미지 유지
		String url = user.getProfileImage(); // 기존 이미지 URL 유지
		if (userProfileUpdateRequest.getProfileImage() != null && !userProfileUpdateRequest.getProfileImage()
			.isEmpty()) {
			if (!isNoImage(user.getProfileImage())) {
				s3Service.deleteObject(user.getProfileImage()); // 기존 이미지 삭제
			}
			url = s3Service.streamUpload("profile", userProfileUpdateRequest.getProfileImage()); // 새로운 이미지 업로드
		}

		// 닉네임이 null이거나 비어 있지 않으면 업데이트, 그렇지 않으면 기존 닉네임 유지
		String nickname = user.getNickname();
		if (userProfileUpdateRequest.getNickname() != null && !userProfileUpdateRequest.getNickname().isEmpty()) {
			nickname = userProfileUpdateRequest.getNickname();
		}

		// 소개글이 null이거나 비어 있지 않으면 업데이트, 그렇지 않으면 기존 소개글 유지
		String introduce = user.getIntroduce();
		if (userProfileUpdateRequest.getIntroduce() != null && !userProfileUpdateRequest.getIntroduce().isEmpty()) {
			introduce = userProfileUpdateRequest.getIntroduce();
		}

		// 수정된 정보로 사용자 정보 업데이트
		user.modifyUserProfile(nickname, url, introduce);

		// 업데이트된 유저 정보 반환
		return UserMapper.INSTANCE.userToUserProfileUpdateResponse(user);
	}

	@Override
	public boolean isNoImage(String profileImage) {
		String[] BASE_IMAGE_URL = {"profile/no_image_1", "profile/no_image_2", "profile/no_image_3",
			"profile/no_image_4", "profile/no_image_5"};

		for (String path : BASE_IMAGE_URL) {
			if (path.equals(profileImage)) {
				return true;
			}
		}

		return false;
	}

}
