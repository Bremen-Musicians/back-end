package com.bremen.backend.domain.article.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bremen.backend.domain.article.dto.CommentRelationResponse;
import com.bremen.backend.domain.article.dto.CommentRequest;
import com.bremen.backend.domain.article.dto.CommentResponse;
import com.bremen.backend.domain.article.dto.CommentUpdateRequest;
import com.bremen.backend.domain.article.service.CommentService;
import com.bremen.backend.global.response.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Tag(name = "Comment", description = "댓글 API")
public class CommentController {
	private final CommentService commentService;

	@PostMapping()
	@Operation(summary = "댓글을 등록합니다.", description = "댓글의 내용, 게시글의 id값 대댓글인 경우 부모댓글의 id를 파라미터로 받습니다.")
	ResponseEntity<Response<CommentResponse>> commentAdd(@Valid @RequestBody CommentRequest commentRequest) {
		CommentResponse commentResponse = commentService.addComment(commentRequest);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "댓글 등록 성공", commentResponse));
	}

	@PatchMapping()
	@Operation(summary = "댓글을 수정합니다.", description = "댓글의 id값과 내용을 파라미터로 받습니다.")
	ResponseEntity<Response<CommentResponse>> commentModify(
		@Valid @RequestBody CommentUpdateRequest commentRequest) {
		CommentResponse commentResponse = commentService.modifyComment(commentRequest);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "댓글 수정 성공", commentResponse));
	}

	@DeleteMapping()
	@Operation(summary = "댓글을 삭제합니다.", description = "댓글의 id값을 파라미터로 받습니다.")
	ResponseEntity<Response<Long>> commentRemove(@RequestParam("id") Long id) {
		Long commentId = commentService.removeComment(id);
		return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), "댓글 삭제 성공", commentId));
	}

	@GetMapping()
	@Operation(summary = "게시글의 댓글을 조회합니다.", description = "게시글의 id값을 파라미터로 받습니다.")
	ResponseEntity<Response<Page>> commentList(@RequestParam("id") Long id,
		Pageable pageable) {
		Page<CommentRelationResponse> comments = commentService.findCommentsByArticleId(id, pageable);
		return ResponseEntity.ok(
			new Response(HttpStatus.OK.value(), "댓글 조회 성공", comments)
		);
	}

}
