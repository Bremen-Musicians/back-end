package com.bremen.backend.domain.user.service;

import com.bremen.backend.domain.user.entity.PrincipalDetails;

public interface FollowUserService {
	boolean followUser(PrincipalDetails principalDetails, String nickname);
}
