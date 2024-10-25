package com.bremen.backend.domain.user.service;

import com.bremen.backend.domain.user.dto.UserReissueResponse;
import com.bremen.backend.domain.user.entity.PrincipalDetails;

public interface ReissueService {
	UserReissueResponse reissueAccessToken(PrincipalDetails principalDetails, String refreshToken);
}
