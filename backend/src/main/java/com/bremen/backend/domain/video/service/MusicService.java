package com.bremen.backend.domain.video.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bremen.backend.domain.video.dto.MusicResponse;
import com.bremen.backend.domain.video.entity.Music;

public interface MusicService {
	Music getMusicById(Long musicId);

	Page<MusicResponse> searchMusicsByTitle(String title, Pageable pageable);
}
