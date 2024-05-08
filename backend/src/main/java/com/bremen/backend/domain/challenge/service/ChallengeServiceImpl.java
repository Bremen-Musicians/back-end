package com.bremen.backend.domain.challenge.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bremen.backend.domain.challenge.dto.ChallengeRequest;
import com.bremen.backend.domain.challenge.dto.ChallengeResponse;
import com.bremen.backend.domain.challenge.entity.Challenge;
import com.bremen.backend.domain.challenge.mapper.ChallengeMapper;
import com.bremen.backend.domain.challenge.repository.ChallengeRepository;
import com.bremen.backend.domain.video.service.MusicService;
import com.bremen.backend.global.CustomException;
import com.bremen.backend.global.infra.s3.service.S3Service;
import com.bremen.backend.global.response.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {
	private final ChallengeRepository challengeRepository;
	private final MusicService musicService;
	private final S3Service s3Service;

	@Override
	public Challenge getChallengeById(Long id) {
		return challengeRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
	}

	@Override
	@Transactional
	public ChallengeResponse addChallenge(ChallengeRequest challengeRequest, MultipartFile mainImage,
		MultipartFile challengeImage) throws IOException {
		Challenge challenge = ChallengeMapper.INSTANCE.challengeRequestToChallenge(challengeRequest);
		String mainImageUrl = s3Service.streamUpload("challenge", mainImage);
		String challengeImageUrl = s3Service.streamUpload("challenge", challengeImage);
		challenge.saveChallenge(musicService.getMusicById(challenge.getMusic().getId()), mainImageUrl,
			challengeImageUrl);
		return ChallengeMapper.INSTANCE.challengeToChallengeResponse(challengeRepository.save(challenge));
	}

	@Override
	@Transactional
	public ChallengeResponse modifyChallenge(Long id, MultipartFile mainImage, MultipartFile challengeImage) throws
		IOException {
		Challenge challenge = getChallengeById(id);
		String mainImageUrl = challenge.getMainImage();
		String challengeImageUrl = challenge.getChallengeImage();
		if (!mainImage.isEmpty()) {
			s3Service.deleteObject(mainImageUrl);
			mainImageUrl = s3Service.streamUpload("challenge", mainImage);
		}
		if (!challengeImage.isEmpty()) {
			s3Service.deleteObject(challengeImageUrl);
			challengeImageUrl = s3Service.streamUpload("challenge", challengeImage);
		}
		challenge.modifyChallenge(mainImageUrl, challengeImageUrl);
		return ChallengeMapper.INSTANCE.challengeToChallengeResponse(challenge);
	}

	@Override
	@Transactional
	public Long removeChallenge(Long id) {
		Challenge challenge = getChallengeById(id);
		s3Service.deleteObject(challenge.getMainImage());
		s3Service.deleteObject(challenge.getChallengeImage());
		challenge.deleteChallenge();
		return challenge.getId();
	}

	@Override
	public ChallengeResponse findLatestChallenge() {
		Challenge latestChallenge = challengeRepository.findFirstByOrderByEndTimeDesc()
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
		return ChallengeMapper.INSTANCE.challengeToChallengeResponse(latestChallenge);
	}
}