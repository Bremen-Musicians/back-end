package com.bremen.backend.domain.video.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bremen.backend.domain.video.dto.MusicResponse;
import com.bremen.backend.domain.video.entity.Music;
import com.bremen.backend.domain.video.mapper.MusicMapper;
import com.bremen.backend.domain.video.repository.MusicRepository;
import com.bremen.backend.global.CustomException;
import com.bremen.backend.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {
	private final MusicRepository musicRepository;

	@Override
	public Music getMusicById(Long musicId) {
		return musicRepository.findById(musicId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MUSIC));
	}

	@Override
	public Page<MusicResponse> searchMusicsByTitle(String title, Pageable pageable) {
		// 페이징된 Music 엔티티 목록을 가져옴
		Page<Music> pages = musicRepository.findByTitleContaining(title, pageable);

		// Page<Music>을 Page<MusicResponse>로 변환하여 반환
		return pages.map(MusicMapper.INSTANCE::musicToMusicResponse);
	}
}