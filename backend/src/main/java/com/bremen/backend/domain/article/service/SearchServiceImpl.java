package com.bremen.backend.domain.article.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bremen.backend.domain.article.dto.ArticleResponse;
import com.bremen.backend.domain.article.entity.Article;
import com.bremen.backend.domain.article.mapper.ArticleMapper;
import com.bremen.backend.domain.article.repository.ArticleCategory;
import com.bremen.backend.domain.article.repository.ArticleOrderBy;
import com.bremen.backend.domain.article.repository.ArticleSearchQueryDslRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
	private final ArticleSearchQueryDslRepository articleSearchQueryDslRepository;

	@Override
	@Transactional(readOnly = true)
	public Page<ArticleResponse> searchArticle(ArticleCategory category, ArticleOrderBy order, List<Long> instrumentIds,
		String keyword, Pageable pageable) {

		Page<Article> pages = articleSearchQueryDslRepository.searchAll(category, order, instrumentIds, keyword, pageable);

		return pages.map(ArticleMapper.INSTANCE::articleToArticleResponse);
	}
}
