package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserInfoDTO {

    private String userId;

    private String userPassword;

    private String userEmail;

    private String userName;

    private String userNickname;

    private String userAge;

    private String userGender;

    private String userInterest;

    private String memberSince;

    // 회원가입시, 중복가입을 방지 위해 사용할 변수
    // DB를 조회해서 회원이 존재하면 Y값을 반환함
    // DB테이블에 존재하지 않는 가상의 컬럼(ALIAS)
    private String existsYn;

    // 이메일 중복체크를 위한 인증번호
    private int authNumber;

    // 외래킨들
    private String fileSeq;
    private String boardSeq;
    private String commentSeq;
    private String challengeSeq;
    private String checkSeq;
    private String testSeq;
}