package com.bremen.backend.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

	//400 BAD_REQUEST 잘못된 요청
	INVALID_PARAMETER(400, "파라미터 값을 확인해주세요."),
	INVALID_FILE_PARAMETER(400, "파일의 값을 확인해주세요."),
	TERMS_OF_SERVICE_AGREEMENT_REQUIRED_ERROR(400, "약관에 동의하지않으면 서비스를 이용할 수 없습니다."),
	INVALID_USER(400, "잘못된 접근입니다."),
	NO_VIDEO_FILE_ATTACHED(400, "비디오 파일이 첨부되지 않았습니다."),
	NO_THUMBNAIL_FILE_ATTACHED(400, "썸네일 파일이 첨부되지 않았습니다."),
	NO_FILE_ATTACHED(400, "파일이 첨부되지 않았습니다."),
	NO_ENSEMBLE_VIDEO_UPLOADED(400, "합주 영상이 추가되지 않았습니다."),
	CANNOT_FOLLOW_SELF(400, "자기 자신을 팔로우할 수 없습니다."),

	//403
	UNAUTHORIZED_S3_ERROR(403, "S3 접근 권한을 확인해주세요."),

	UNAUTHORIZED_ARTICLE_ACCESS(403, "게시글 삭제 권한이 없습니다."),

	UNAUTHORIZED_COMMENT_ACCESS(403, "댓글 접근 권한이 없습니다."),

	UNAUTHORIZED(403, "접근 권한 없음"),

	//404 NOT_FOUND 잘못된 리소스 접근
	NOT_FOUND_USER(404, "해당하는 사용자가 없습니다."),

	NOT_FOUND_ACCESS_TOKEN(404, "액세스 토큰이 유효하지 않습니다."),

	NOT_FOUND_REFRESH_TOKEN(404, "리프레시 토큰이 유효하지 않습니다."),

	NOT_FOUND_VIDEO(404, "존재하지 않는 영상입니다."),

	NOT_FOUND_MUSIC(404, "존재하지 않는 음악입니다."),

	NOT_FOUND_ARTICLE(404, "존재하지 않는 게시글입니다."),

	NOT_FOUND_COMMENT(404, "존재하지 않는 댓글입니다."),

	NOT_FOUND_CHALLENGE(404, "존재하지 않는 챌린지입니다."),

	NOT_FOUND_INSTRUMENT(404, "존재하지 않는 악기입니다."),

	NOT_FOUND_NOTIFICATION(404, "존재하지 않는 알림 메시지입니다."),

	//409 CONFLICT 중복된 리소스
	CONFLICT_USER(409, "중복된 유저입니다"),

	//500 INTERNAL SERVER ERROR
	ALARM_CONNECT_ERROR(500, "알림 서버 연결이 되지 않았습니다."),

	INTERNAL_SERVER_ERROR(500, "서버 에러입니다.");

	private final int status;
	private final String message;
}
