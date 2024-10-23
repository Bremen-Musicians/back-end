package com.bremen.backend.domain.video.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bremen.backend.domain.video.dto.VideoRequest;
import com.bremen.backend.domain.video.dto.VideoResponse;
import com.bremen.backend.domain.video.service.VideoService;
import com.bremen.backend.global.response.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/videos")
@Tag(name = "Video", description = "영상 API")
public class VideoController {
	private final VideoService videoService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "영상 정보를 등록합니다.", description = "영상과 영상정보를 입력받아 영상 데이터를 등록합니다.")
	ResponseEntity<Response<VideoResponse>> videoAdd(
		@RequestPart(value = "videoInfo") VideoRequest videoRequest,
		@RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
		@RequestPart(value = "video") MultipartFile video,
		@RequestPart(value = "highlight", required = false) MultipartFile highlight) throws IOException {
		VideoResponse videoResponse = videoService.addVideo(videoRequest, thumbnail, video, highlight);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "동영상이 정상적으로 등록되었습니다.", videoResponse));
	}
}
