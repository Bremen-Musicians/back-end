package com.bremen.backend.domain.article.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bremen.backend.domain.article.dto.CommentRelationResponse;
import com.bremen.backend.domain.article.dto.CommentRequest;
import com.bremen.backend.domain.article.dto.CommentResponse;
import com.bremen.backend.domain.article.dto.CommentUpdateRequest;
import com.bremen.backend.domain.article.entity.Comment;
import com.bremen.backend.domain.user.entity.PrincipalDetails;

public interface CommentService {
	Comment getCommentById(Long commentId);

	CommentResponse addComment(PrincipalDetails principalDetails, CommentRequest commentRequest);

	CommentResponse modifyComment(PrincipalDetails principalDetails, CommentUpdateRequest commentRequest);

	Long removeComment(PrincipalDetails principalDetails, Long id);

	Page<CommentRelationResponse> findCommentsByArticleId(Long id, Pageable pageable);
}
