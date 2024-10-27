package com.bremen.backend.domain.video.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bremen.backend.domain.user.entity.PrincipalDetails;
import com.bremen.backend.domain.video.dto.VideoRequest;
import com.bremen.backend.domain.video.dto.VideoResponse;
import com.bremen.backend.domain.video.entity.Video;
import com.bremen.backend.domain.video.mapper.VideoMapper;
import com.bremen.backend.domain.video.repository.VideoRepository;
import com.bremen.backend.global.CustomException;
import com.bremen.backend.global.infra.s3.service.S3Service;
import com.bremen.backend.global.response.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
	private final VideoRepository videoRepository;
	private final S3Service s3Service;
	private final EnsembleService ensembleService;
	private final MusicService musicService;
	private final InstrumentService instrumentService;

	@Override
	public VideoResponse findVideoById(Long videoId) {
		return VideoMapper.INSTANCE.videoToVideoResponse(getVideoById(videoId));
	}

	@Override
	public Video getVideoById(Long videoId) {
		return videoRepository.findById(videoId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VIDEO));
	}

	@Override
	@Transactional
	public VideoResponse addVideo(PrincipalDetails principalDetails, VideoRequest videoRequest,
		MultipartFile thumbnailFile, MultipartFile videoFile,
		MultipartFile highlightFile) throws
		IOException {

		Video video = VideoMapper.INSTANCE.videoRequestToVideo(videoRequest);

		video.setSavedVideo(principalDetails.getUser(),
			thumbnailFile == null || thumbnailFile.isEmpty() ? null :
				s3Service.streamUpload("thumbnail", thumbnailFile),
			musicService.getMusicById(videoRequest.getMusicId()),
			instrumentService.getInstrumentById(videoRequest.getInstrumentId()));
		// 영상 기본 정보 세팅

		// boolean isHighLight = highlightFile.isEmpty();
		// video = saveVideo(video, isHighLight ? highlightFile : videoFile, !isHighLight);
		// 하이라이트 영상 or 일반 영상

		video.setSavedVideo(false, s3Service.streamUpload("video", videoFile));
		Video savedVideo = videoRepository.save(video);

		if (videoRequest.isEnsemble()) {
			saveEnsemble(videoRequest, savedVideo);
		}
		// 합주 영상 저장

		return VideoMapper.INSTANCE.videoToVideoResponse(savedVideo);
	}

	@Override
	@Transactional
	public Long removeVideo(Long videoId) {
		Video video = getVideoById(videoId);
		video.deleteVideo();
		return video.getId();
	}

	public Video saveVideo(Video video, MultipartFile multipartFile, boolean isHighLight) throws IOException {
		String videoUrl = s3Service.streamUpload("video", multipartFile);
		video.setSavedVideo(isHighLight, videoUrl);
		return video;
	}

	public void saveEnsemble(VideoRequest videoRequest, Video savedVideo) {
		if (videoRequest.getEnsembleList() == null || videoRequest.getEnsembleList().isEmpty()) {
			throw new CustomException(ErrorCode.NO_ENSEMBLE_VIDEO_UPLOADED);
		}

		for (Long id : videoRequest.getEnsembleList()) {
			Video ensembleVideo = getVideoById(id);
			ensembleService.addEnsemble(savedVideo, ensembleVideo);
		}
	}

}
