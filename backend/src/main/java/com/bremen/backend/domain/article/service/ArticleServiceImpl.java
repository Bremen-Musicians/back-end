package com.bremen.backend.domain.article.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bremen.backend.domain.article.dto.ArticleRequest;
import com.bremen.backend.domain.article.dto.ArticleResponse;
import com.bremen.backend.domain.article.dto.ArticleUpdateRequest;
import com.bremen.backend.domain.article.entity.Article;
import com.bremen.backend.domain.article.mapper.ArticleMapper;
import com.bremen.backend.domain.article.repository.ArticleOrderBy;
import com.bremen.backend.domain.article.repository.ArticleQueryDslRepository;
import com.bremen.backend.domain.article.repository.ArticleQueryRepository;
import com.bremen.backend.domain.article.repository.ArticleRepository;
import com.bremen.backend.domain.challenge.service.ChallengeArticleService;
import com.bremen.backend.domain.challenge.service.ChallengeService;
import com.bremen.backend.domain.user.entity.User;
import com.bremen.backend.domain.user.service.UserService;
import com.bremen.backend.domain.video.entity.Video;
import com.bremen.backend.domain.video.service.EnsembleService;
import com.bremen.backend.domain.video.service.VideoService;
import com.bremen.backend.global.CustomException;
import com.bremen.backend.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
	private final ArticleRepository articleRepository;
	private final ArticleHashtagService articleHashtagService;
	private final ArticleQueryRepository articleQueryRepository;
	private final ArticleQueryDslRepository articleQueryDslRepository;

	private final UserService userService;
	private final LikeService likeService;
	private final VideoService videoService;
	private final EnsembleService ensembleService;
	private final ChallengeService challengeService;
	private final ChallengeArticleService challengeArticleService;

	@Override
	@Transactional(readOnly = true)
	public ArticleResponse findArticleById(Long articleId) {
		Article article = getArticleById(articleId);
		article.viewArticle();
		ArticleResponse articleResponse = ArticleMapper.INSTANCE.articleToArticleResponse(article);

		if (userService.isAuthenticated()) {
			Long userId = userService.getUserByToken().getId();
			boolean isLike = likeService.isLikeArticle(userId, articleId);
			articleResponse.setLike(isLike);
		}
		return articleResponse;
	}

	@Override
	public Article getArticleById(Long articleId) {
		return articleRepository.findById(articleId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE));
	}

	@Override
	@Transactional
	public ArticleResponse addArticle(ArticleRequest articleRequest) {
		Article article = ArticleMapper.INSTANCE.articleRequestToArticle(articleRequest);
		User user = userService.getUserByToken();
		Video video = videoService.getVideoById(articleRequest.getVideoId());
		article.saveArticle(user, video);
		Article savedArticle = articleRepository.save(article);

		if (savedArticle.isChallenge()) {
			challengeArticleService.addChallengeArticle(savedArticle);
		}

		ArticleResponse articleResponse = ArticleMapper.INSTANCE.articleToArticleResponse(savedArticle);
		articleResponse.setHashtags(articleHashtagService.addHashtags(article, articleRequest.getHashtags()));

		return articleResponse;
	}

	@Override
	@Transactional
	public ArticleResponse modifyArticle(ArticleUpdateRequest articleUpdateRequest) {
		Article article = getArticleById(articleUpdateRequest.getId());
		article.modifyArticle(articleUpdateRequest.getTitle(), articleUpdateRequest.getContent());
		ArticleResponse articleResponse = ArticleMapper.INSTANCE.articleToArticleResponse(article);
		articleResponse.setHashtags(articleHashtagService.modifyHashtags(article, articleUpdateRequest.getHashtags()));
		return articleResponse;
	}

	@Override
	@Transactional
	public Long removeArticle(Long id) {
		Article article = getArticleById(id);
		User user = userService.getUserByToken();
		if (!article.getUser().getId().equals(user.getId())) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_ARTICLE_ACCESS);
		}
		article.deleteArticle();
		videoService.removeVideo(article.getVideo().getId());
		articleHashtagService.removeHashtags(article.getId());
		return article.getId();
	}

	@Override
	public List<ArticleResponse> findEnsembleArticles(Long musicId, List<Long> instrumentsIds, String category,
		String keyword) {
		List<Article> articles = articleQueryRepository.findEnsembleArticle(musicId, instrumentsIds, category, keyword);
		return articles.stream().map(ArticleMapper.INSTANCE::articleToArticleResponse).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ArticleResponse> findArticleByNickname(String nickname, Pageable pageable) {
		return articleRepository.findArticlesByNickname(nickname, pageable)
			.map(ArticleMapper.INSTANCE::articleToArticleResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ArticleResponse> findArticle(ArticleOrderBy articleOrderBy, Pageable pageable) {
		LocalDateTime dateTime = LocalDateTime.now().minusDays(7);
		Page<Article> pages;

		if (userService.isAuthenticated()) {
			User user = userService.getUserByToken();
			pages = articleQueryDslRepository.findArticle(user.getId(), articleOrderBy, dateTime, pageable);
		} else {
			pages = articleQueryDslRepository.findArticle(articleOrderBy, dateTime, pageable);
		}

		return pages.map(ArticleMapper.INSTANCE::articleToArticleResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ArticleResponse> findRelatedArticle(Long id, Pageable pageable) {
		Page<Article> pages = articleQueryDslRepository.findRelatedArticle(id, pageable);
		return pages.map(ArticleMapper.INSTANCE::articleToArticleResponse);
	}

	@Override
	public Article findArticlesByVideo(Video video) {
		return articleRepository.findByVideo(video);
	}

	@Override
	@Transactional
	public ArticleResponse addChallengeEnsembleArticle(ArticleRequest articleRequest) {
		ArticleResponse articleResponse = addArticle(articleRequest);
		Article article = getArticleById(articleResponse.getId());
		challengeService.registEnsemble(article);
		challengeArticleService.regiestWinners(
			ensembleService.getEnsembleVideoList(videoService.getVideoById(article.getVideo().getId()))
				.stream()
				.map(this::findArticlesByVideo)
				.collect(Collectors.toList())
		);
		return articleResponse;
	}

}
