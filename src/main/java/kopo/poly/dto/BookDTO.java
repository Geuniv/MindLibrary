package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDTO {

    // 네이버 책 검색 API 응답 필드들
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;

    // 책 검색 결과를 담을 리스트
    private List<BookSearchDTO> items;

    // 검색 파라미터로 사용할 필드들
    private String title;
}
