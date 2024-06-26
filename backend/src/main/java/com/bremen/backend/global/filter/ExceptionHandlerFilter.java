package com.bremen.backend.global.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bremen.backend.global.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			setErrorResponse(HttpStatus.BAD_REQUEST, response, e);
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			setErrorResponse(HttpStatus.BAD_REQUEST, response, e);
		} catch (IllegalArgumentException e) {
			setErrorResponse(HttpStatus.BAD_REQUEST, response, e);
		} catch (AccessDeniedException e) {
			setErrorResponse(HttpStatus.UNAUTHORIZED, response, e);
		}
	}

	public void setErrorResponse(HttpStatus status, HttpServletResponse res, Throwable ex) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		res.setStatus(status.value());
		res.setContentType("application/json; charset=UTF-8");
		ErrorResponse errorResponse = new ErrorResponse(status.value(), ex.getMessage());

		res.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}

}
