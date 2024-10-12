package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverDTO {

    private ResponseDTO response;

    @Data
    public static class ResponseDTO {

        private String id; // 네이버 아이디

        private String name; // 네이버 사용자 이름

        private String email; // 네이버 이메일

        private String gender; // 네이버 성별

        private String nickname; // 네이버 닉네임

        private  String birthyear; // 네이버 생년

        private String birthday; // 네이버 생일

        private String age; // 나이

        @JsonProperty("profile_image")
        private String profileImage; // 네이버 프로필 사진

//        private String userInterest = "none";              // 기본값 설정
//
//        private String fileUrl = "";

    }
}
