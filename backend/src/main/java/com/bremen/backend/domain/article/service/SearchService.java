package com.bremen.backend.domain.article.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bremen.backend.domain.article.dto.ArticleResponse;
import com.bremen.backend.domain.article.repository.ArticleCategory;
import com.bremen.backend.domain.article.repository.ArticleOrderBy;

public interface SearchService {
	Page<ArticleResponse> searchArticle(ArticleCategory category, ArticleOrderBy order, List<Long> instrumentIds, String keyword,
		Pageable pageable);
}
