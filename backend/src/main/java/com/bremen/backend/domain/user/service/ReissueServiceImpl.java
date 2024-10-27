package com.bremen.backend.domain.user.service;

import org.springframework.stereotype.Service;

import com.bremen.backend.domain.user.dto.UserReissueResponse;
import com.bremen.backend.domain.user.entity.PrincipalDetails;
import com.bremen.backend.domain.user.entity.Token;
import com.bremen.backend.domain.user.entity.User;
import com.bremen.backend.domain.user.mapper.TokenMapper;
import com.bremen.backend.global.common.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReissueServiceImpl implements ReissueService {
	private final TokenService tokenService;
	private final JwtTokenUtil jwtTokenUtil;

	@Override
	public UserReissueResponse reissueAccessToken(PrincipalDetails principalDetails, String refreshToken) {
		Token token = tokenService.findByRefreshTokenAndDelete(refreshToken);
		//액세스 토큰 추출을 위해서 redis에서 데이터를 가져옴

		String username = jwtTokenUtil.extractUsername(token.getAccessToken());
		//유저네임 추출

		User user = principalDetails.getUser();
		//데이터베이스에 있는 정보를 확실하게 추출하여 access token을 발급하기 위해 user 데이터 추출

		String accessToken = jwtTokenUtil.createToken(user);

		String newRefreshToken = jwtTokenUtil.createRefreshToken();

		tokenService.saveTokenInfo(user.getUsername(), refreshToken, accessToken);

		UserReissueResponse userReissueResponse = TokenMapper.INSTANCE.tokenToUserReissue(
			Token.builder()
				.accessToken(accessToken)
				.refreshToken(newRefreshToken)
				.username(username)
				.build()
		);
		return userReissueResponse;
	}
}
