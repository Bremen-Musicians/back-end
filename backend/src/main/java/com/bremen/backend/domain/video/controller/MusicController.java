package com.bremen.backend.domain.video.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bremen.backend.domain.video.dto.MusicResponse;
import com.bremen.backend.domain.video.service.MusicService;
import com.bremen.backend.global.response.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/musics")
@Tag(name = "Music", description = "음악 API")
public class MusicController {
	private final MusicService musicService;

	@GetMapping("/search")
	@Operation(summary = "키워드로 음악을 조회합니다.", description = "음악을 검색하기 위한 제목 키워드를 파라미터로 받습니다.")
	ResponseEntity<Response> musicsSearch(@RequestParam("title") String title, Pageable pageable) {
		Page<MusicResponse> musics = musicService.searchMusicsByTitle(title, pageable);
		return ResponseEntity.ok(
			new Response<>(HttpStatus.OK.value(), "음악 조회 성공", musics));
	}

}
