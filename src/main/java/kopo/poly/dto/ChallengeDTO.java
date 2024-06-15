package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record ChallengeDTO (

        String challengeSeq, // 도전번호

        String userId, // 유저아이디

        String userNickname, // 유저닉네임

        String challengeDivision, // 도전 구분

        String challengeComment, // 도전 내용
        
        String challengeRegDt, // 생성일

        String challengeStartDate, // 시작일

        String challengeEndDate, // 종료일
        
        String currentDay // 일차 계산

) {
}
