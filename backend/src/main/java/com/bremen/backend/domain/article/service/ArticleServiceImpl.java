package com.bremen.backend.domain.article.service;

import org.springframework.stereotype.Service;

import com.bremen.backend.domain.article.dto.ArticleRequest;
import com.bremen.backend.domain.article.dto.ArticleResponse;
import com.bremen.backend.domain.article.entity.Article;
import com.bremen.backend.domain.article.mapper.ArticleMapper;
import com.bremen.backend.domain.article.repository.ArticleRepository;
import com.bremen.backend.domain.user.service.UserService;
import com.bremen.backend.domain.video.service.VideoService;
import com.bremen.backend.global.CustomException;
import com.bremen.backend.global.response.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
	private final ArticleRepository articleRepository;
	private final UserService userService;
	private final VideoService videoService;

	@Override
	public ArticleResponse findArticleById(Long articleId) {
		return ArticleMapper.INSTANCE.articleToArticleResponse(getArticleById(articleId));
	}

	@Override
	public Article getArticleById(Long articleId) {
		return articleRepository.findByIdWithUserAndVideo(articleId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE));
	}

	@Override
	@Transactional
	public ArticleResponse addArticle(ArticleRequest articleRequest) {
		Article article = ArticleMapper.INSTANCE.articleRequestToArticle(articleRequest);
		article.saveArticle(userService.getUserByToken(), videoService.getVideoById(articleRequest.getVideoId()));
		Article savedArticle = articleRepository.save(article);
		return ArticleMapper.INSTANCE.articleToArticleResponse(savedArticle);
	}
}
