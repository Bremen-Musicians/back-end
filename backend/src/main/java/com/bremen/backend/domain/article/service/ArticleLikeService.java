package com.bremen.backend.domain.article.service;

import com.bremen.backend.domain.user.entity.PrincipalDetails;

public interface ArticleLikeService {
	int toggleLikeArticle(PrincipalDetails principalDetails, Long id);
}
