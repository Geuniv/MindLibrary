package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record HospitalDTO(

        String addr, // 주소

        String telno, // 전화번호

        String hospUrl, // 홈페이지

        String estbDd, // 개설일자

        String postNo, //우편번호
        
        String sidoCd, //시도 코드

        String sgguCd, // 시군구 코드

        String emdongNm, // 읍면동명

        String yadmNm, // 병원명(UTF-8 인코딩 필요)

        String zipCd, //  분류코드

        String clCd, // 종별코드

        String clCdNm, // 종별코드명

        String dgsbjtCd, // 진료과목코드

        String xPos, // x좌표(소수점 15)

        String yPos, // y좌표(소수점 15)

        String radius, // 단위 : 미터(m)

        GeoJsonPoint location, // 위도, 경도 묶은 거

        Double range // 반경
){
}
