package com.bremen.backend.domain.article.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bremen.backend.domain.article.dto.ArticleResponse;
import com.bremen.backend.domain.article.repository.ArticleCategory;
import com.bremen.backend.domain.article.repository.ArticleOrderBy;
import com.bremen.backend.domain.article.service.SearchService;
import com.bremen.backend.global.response.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
@Tag(name = "Search", description = "게시글 검색 API")
public class SearchController {
	private final SearchService searchService;

	@GetMapping("/search")
	@Operation(summary = "게시글 검색 기능을 수행합니다.",
		description = "Category: 전체(ALL-default)/곡명(MUSIC)/제목(TITLE)/아티스트(ARTIST)/작성자(WRITER), "
			+ "Order: 최신순(Latest-default)/인기순(Popular), "
			+ "instrumentIds: 악기id 목록, "
			+ "keyword: 검색어")
	ResponseEntity<Response<Page>> articlesSearch(
		@RequestParam(defaultValue = "ALL") ArticleCategory category,
		@RequestParam(defaultValue = "POPULAR") ArticleOrderBy order,
		@RequestParam(required = false) List<Long> instrumentIds,
		@RequestParam(value = "keyword", required = false) String keyword,
		Pageable pageable) {
		Page<ArticleResponse> responses = searchService.searchArticle(category, order, instrumentIds, keyword, pageable);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(),"게시글 검색 성공",responses));
	}
}
