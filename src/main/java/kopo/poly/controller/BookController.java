package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.dto.BookDTO;
import kopo.poly.service.IBookService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "book")
@Controller
public class BookController {

    private final IBookService bookService;

    @GetMapping("/searchBookForm")
    public String searchBookForm() throws Exception {
        return "book/searchBookForm"; // searchBookForm.html 뷰를 반환
    }

    @PostMapping("/getBook")
    @ResponseBody
    public BookDTO searchBook(@RequestParam String title) throws Exception {
        try {
            log.info("getBook 호출");
            log.info("검색 제목: {}", title);

            // BookService의 getBook 메서드를 호출하여 책 정보를 가져옴
            BookDTO bookDTO = bookService.getBook(title);

            log.info("검색된 책 정보: {}", bookDTO);

            return bookDTO; // 검색된 책 정보를 반환

        } catch (Exception e) {
            log.error("책 검색 중 오류 발생", e);
            return null; // 예외 발생 시 null 반환
        }
    }

    @GetMapping("/getBookResult")
    public String searchBookResult(@RequestParam String title, ModelMap model) throws Exception {
        try {
            log.info("getBook 호출");
            log.info("검색 제목: {}", title);

            // BookService의 getBook 메서드를 호출하여 책 정보를 가져옴
            BookDTO bookDTO = bookService.getBook(title);

            log.info("검색된 책 정보: {}", bookDTO);

            model.addAttribute("book", bookDTO); // 검색된 책 정보를 모델에 추가

            return "book/searchResult"; // searchResult.html 뷰를 반환

        } catch (Exception e) {
            log.error("책 검색 중 오류 발생", e);
            return "error"; // 예외 발생 시 error.html 뷰 반환
        }
    }
}
