package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BookSearchDTO {

    // 책 제목
    private String title;

    // 링크
    private String link;

    // 이미지
    private String image;

    // 저자
    private String author;

    // 가격
    private String discount;

    // 출판사
    private String publisher;

    // 발행날짜
    private String pubdate;

    // 국제표준도서번호
    private String isbn;

    // 설명
    private String description;

}
