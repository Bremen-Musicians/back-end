package com.bremen.backend.domain.article.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bremen.backend.domain.article.dto.CommentRelationResponse;
import com.bremen.backend.domain.article.dto.CommentRequest;
import com.bremen.backend.domain.article.dto.CommentResponse;
import com.bremen.backend.domain.article.dto.CommentUpdateRequest;
import com.bremen.backend.domain.article.entity.Article;
import com.bremen.backend.domain.article.entity.Comment;
import com.bremen.backend.domain.article.mapper.CommentMapper;
import com.bremen.backend.domain.article.repository.CommentRepository;
import com.bremen.backend.domain.user.entity.PrincipalDetails;
import com.bremen.backend.domain.user.entity.User;
import com.bremen.backend.global.CustomException;
import com.bremen.backend.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	private final CommentRepository commentRepository;
	private final ArticleService articleService;

	@Override
	public Comment getCommentById(Long commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));
	}

	@Override
	@Transactional
	public CommentResponse addComment(PrincipalDetails principalDetails, CommentRequest commentRequest) {
		Comment comment = CommentMapper.INSTANCE.commentRequestToComment(commentRequest);
		User user = principalDetails.getUser();
		Article article = articleService.getArticleById(commentRequest.getArticleId());
		if (commentRequest.getGroupId() != null) {
			comment.saveComment(user, article, getCommentById(commentRequest.getGroupId()));
		} else {
			comment.saveComment(user, article, null);
		}
		return CommentMapper.INSTANCE.commentToCommentResponse(commentRepository.save(comment));
	}

	@Override
	@Transactional
	public CommentResponse modifyComment(PrincipalDetails principalDetails, CommentUpdateRequest commentRequest) {
		Comment comment = getCommentById(commentRequest.getId());
		checkCommentAccessRights(comment.getUser(), principalDetails);
		comment.modifyContent(commentRequest.getContent());
		return CommentMapper.INSTANCE.commentToCommentResponse(comment);
	}

	@Override
	@Transactional
	public Long removeComment(PrincipalDetails principalDetails, Long id) {
		Comment comment = getCommentById(id);
		checkCommentAccessRights(comment.getUser(), principalDetails);

		if (isParentComment(comment)) {
			deleteParentComment(comment);
		} else {
			deleteChildComment(comment);
		}
		return id;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CommentRelationResponse> findCommentsByArticleId(Long id, Pageable pageable) {
		Page<Comment> commentsPage = commentRepository.findByArticleIdOrderByGroupAscCreateTimeDesc(id, pageable);

		List<Comment> comments = commentsPage.getContent();
		Map<Long, CommentRelationResponse> parents = new LinkedHashMap<>();
		comments.forEach(comment -> {
			CommentRelationResponse commentRelationResponse = CommentMapper.INSTANCE.commentToCommentRelationResponse(
				comment);
			if (isParentComment(comment)) {
				parents.put(comment.getId(), commentRelationResponse);
			} else {
				CommentRelationResponse parent = parents.get(comment.getGroup().getId());
				if (parent.getChildren() == null) {
					parent.setChildren(new ArrayList<>());
				}
				parent.getChildren().add(commentRelationResponse);
				parent.addChildrenCnt();
			}
		});
		List<CommentRelationResponse> result = new ArrayList<>(parents.values());

		return new PageImpl<>(result, pageable, commentsPage.getTotalElements());
	}

	private void checkCommentAccessRights(User user, PrincipalDetails principalDetails) {
		if (!user.equals(principalDetails.getUser())) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
		}
	}

	private boolean isParentComment(Comment comment) {
		return comment.getGroup() == null;
	}

	private void deleteParentComment(Comment comment) {
		if (commentRepository.findByGroupId(comment.getId()).isEmpty()) {
			// 자식 댓글이 존재하지 않는 경우
			commentRepository.deleteById(comment.getId());
		} else {
			// 자식 댓글이 존재하는 경우
			comment.deleteComment();
		}
	}

	private void deleteChildComment(Comment comment) {
		commentRepository.deleteById(comment.getId());
		Comment parentComment = getCommentById(comment.getGroup().getId());
		if (parentComment.isDeleted() && commentRepository.findByGroupId(parentComment.getId()).isEmpty()) {
			// 부모 댓글 삭제 필요시(부모 댓글 삭제됨 + 자식 댓글 없음)
			commentRepository.deleteById(parentComment.getId());
		}
	}
}
