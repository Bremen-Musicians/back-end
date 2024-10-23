package com.bremen.backend.domain.challenge.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bremen.backend.domain.article.dto.ArticleListResponse;
import com.bremen.backend.domain.challenge.dto.ChallengeArticleResponse;
import com.bremen.backend.domain.challenge.dto.ChallengeRequest;
import com.bremen.backend.domain.challenge.dto.ChallengeResponse;
import com.bremen.backend.domain.challenge.service.ChallengeService;
import com.bremen.backend.global.response.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenges")
@Tag(name = "Challenge", description = "챌린지 API")
public class ChallengeController {
	private final ChallengeService challengeService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "챌린지를 등록합니다.", description = "챌린지 곡, 일정, 챌린지 내용을 입력받아 챌린지를 등록합니다.")
	ResponseEntity<Response<ChallengeResponse>> challengeAdd(
		@Valid @RequestPart(value = "challengeInfo") ChallengeRequest challengeRequest,
		@RequestPart(value = "mainImage") MultipartFile mainImage,
		@RequestPart(value = "challengeImage") MultipartFile challengeImage) throws IOException {
		ChallengeResponse challengeResponse = challengeService.addChallenge(challengeRequest, mainImage,
			challengeImage);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "챌린지가 정상적으로 등록되었습니다.", challengeResponse));
	}

	@GetMapping("/latest")
	@Operation(summary = "최신 챌린지를 조회합니다.", description = "가장 최근에 등록된 챌린지를 조회합니다.")
	ResponseEntity<Response<ChallengeResponse>> challengeDetailsLatest() {
		ChallengeResponse challengeResponse = challengeService.findLatestChallengeResponse();
		return ResponseEntity.ok(
			new Response<>(HttpStatus.OK.value(), "최신 챌린지를 정상적으로 조회하였습니다.", challengeResponse));
	}

	@GetMapping("/ensembles")
	@Operation(summary = "지난 챌린지 합주 영상을 조회합니다.")
	ResponseEntity<Response<Page>> challengesEnsemble(Pageable pageable) {
		Page<ChallengeArticleResponse> articles = challengeService.findChallengeEnsemble(pageable);
		return ResponseEntity.ok(
			new Response<>(HttpStatus.OK.value(), "지난 챌린지 선정 게시글 조회 성공", articles)
		);
	}

	@GetMapping()
	@Operation(summary = "챌린지 영상 조회")
	ResponseEntity<Response<Page>> challengesList(@RequestParam(required = false) Long instrumentId, Pageable pageable) {
		Page<ArticleListResponse> articles = challengeService.findChallengeArticle(instrumentId, pageable);
		return ResponseEntity.ok(
			new Response<>(HttpStatus.OK.value(), "챌린지 영상 조회 성공", articles));
	}

	@GetMapping("/winners")
	@Operation(summary = "악기별 1위 챌린지 게시글 조회")
	ResponseEntity<Response<List<ArticleListResponse>>> challengesWinner() {
		List<ArticleListResponse> articles = challengeService.findChallengeWinner();
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "챌린지 우수자의 게시글이 조회되었습니다.", articles));
	}

}
