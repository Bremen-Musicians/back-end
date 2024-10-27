package com.bremen.backend.global.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bremen.backend.global.common.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	private final UserDetailsService userDetailsService;
	private final JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = extractToken(request);

		if (token != null) {
			try {
				validateToken(token); // 토큰 유효성 검사
				setAuthentication(token); // 인증 객체 설정
				response.setHeader("Expired-Token", "false");

			} catch (ExpiredJwtException e) {
				response.setHeader("Expired-Token", "true");
				throw e;
			}
		}

		filterChain.doFilter(request, response);
	}

	private String extractToken(HttpServletRequest request) {
		return jwtTokenUtil.extractToken(request);
	}

	private void validateToken(String token) {
		jwtTokenUtil.isLogoutToken(token); // 로그아웃 토큰 검사
		if (!jwtTokenUtil.validateToken(token)) {
			throw new IllegalArgumentException("유효하지 않은 토큰입니다");
		}
	}

	private void setAuthentication(String token) {
		String username = jwtTokenUtil.extractUsername(token); // 토큰에서 유저네임 추출
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			userDetails, token, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

}