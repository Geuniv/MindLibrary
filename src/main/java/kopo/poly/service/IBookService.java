package kopo.poly.service;

import kopo.poly.dto.BookDTO;

public interface IBookService {

    // 네이버 책 API를 호출하여 책 검색 결과 받아오기
    BookDTO getBook(String title) throws Exception;

}
