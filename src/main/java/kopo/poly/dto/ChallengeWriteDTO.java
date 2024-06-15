package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ChallengeWriteDTO {

    private String challengeSeq; // 마음쓰기 순번

    private String challengeWriteSeq; // 인증 순번

    private String userId; // 유저 아이디

    private String challengeWriteTitle; // 인증 제목

    private String challengeWriteContent; // 인증 내용

    private String challengeWriteBooktitle; // 인증 책제목

    private String challengeWriteBookimage; // 인증 책 url
    
    private String challengeWriteRegDt; // 작성일

//        String challengeWriteStar
}
