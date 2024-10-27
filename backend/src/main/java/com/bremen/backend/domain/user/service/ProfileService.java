package com.bremen.backend.domain.user.service;

import java.io.IOException;

import com.bremen.backend.domain.user.dto.UserProfileRequest;
import com.bremen.backend.domain.user.dto.UserProfileUpdateRequest;
import com.bremen.backend.domain.user.dto.UserProfileUpdateResponse;
import com.bremen.backend.domain.user.entity.PrincipalDetails;

public interface ProfileService {
	void modifyUserProfile(PrincipalDetails principalDetails, UserProfileRequest userProfileRequest) throws IOException;

	UserProfileUpdateResponse modifyUserProfile(
		PrincipalDetails principalDetails, UserProfileUpdateRequest userProfileUpdateRequest) throws IOException;

	boolean isNoImage(String url);
}
