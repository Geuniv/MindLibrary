package kopo.poly.service;

import feign.Param;
import feign.RequestLine;
import kopo.poly.dto.BookDTO;
import kopo.poly.dto.BookSearchDTO;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "BookAPI", url = "https://openapi.naver.com")
public interface INaverAPIService {

    @RequestLine("GET /v1/search/book.json?query={query}&display={display}&sort={sort}")
    BookDTO bookSearch(
            @Param("query") String query,
            @Param("display") Integer display,
            @Param("sort") String sort
    );

}
