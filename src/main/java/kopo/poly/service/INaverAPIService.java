package kopo.poly.service;

import feign.Param;
import feign.RequestLine;
import kopo.poly.dto.BookDTO;
import kopo.poly.dto.BookSearchDTO;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "BookAPI", url = "https://openapi.naver.com")
public interface INaverAPIService {

    // 네이버 책 검색 API ( Open Feign ) - 2024.08.30
    @RequestLine("GET /v1/search/book.json?query={query}&display={display}&sort={sort}")
    BookDTO bookSearch(
            @Param("query") String query,
            @Param("display") Integer display,
            @Param("sort") String sort
    );

}
