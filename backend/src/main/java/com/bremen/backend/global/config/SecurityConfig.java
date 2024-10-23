package com.bremen.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bremen.backend.global.filter.AuthorizationFilter;
import com.bremen.backend.global.filter.ExceptionHandlerFilter;
import com.bremen.backend.global.filter.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final CorsConfig corsConfig;
	private final JwtFilter jwtFilter;
	private final ExceptionHandlerFilter exceptionHandlerFilter;
	private final AuthorizationFilter authorizationFilter;
	private static final String HEAD = "/api/v1";
	private static final String[] PERMIT_URL_ARRAY = {
		/* swagger v3 */
		"/v3/api-docs/**",
		"/swagger-ui/**"
	};
	private static final String[] GET_PERMIT_URL_ARRAY = {
		HEAD + "/users/**",
		HEAD + "/articles/**",
		HEAD + "/articles",
		HEAD + "/challenges/**",
		HEAD + "/challenges",
		HEAD + "/comments"
	};

	private static final String[] POST_PERMIT_URL_ARRAY = {
		HEAD + "/users"
	};

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.sessionManagement(
				(session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(
				auth -> auth
					.requestMatchers(PERMIT_URL_ARRAY).permitAll()
					.requestMatchers("/error").permitAll()
					.requestMatchers("/api/v1/users").permitAll()
					.requestMatchers(HttpMethod.GET, GET_PERMIT_URL_ARRAY).permitAll()
					.requestMatchers(HttpMethod.POST, POST_PERMIT_URL_ARRAY).permitAll()
					.anyRequest().authenticated()
			).cors(
				cors -> cors.configurationSource(corsConfig.corsConfigurationSource())
			)

			.addFilterBefore(
				jwtFilter, UsernamePasswordAuthenticationFilter.class
			)
			.addFilterAfter(
				authorizationFilter, JwtFilter.class
			)
			.addFilterBefore(
				exceptionHandlerFilter, JwtFilter.class
			)
		;

		return httpSecurity.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
