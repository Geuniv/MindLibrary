package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    }
}
