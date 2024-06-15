package kopo.poly.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.BookDTO;
import kopo.poly.service.IBookService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static kopo.poly.util.NetworkUtil.get;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookService implements IBookService {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Override
    public BookDTO getBook(String title) throws Exception {
        log.info("BookService.getBook 시작");
        log.info("검색 제목: {}", title);

        // API URL 구성
        String apiUrl = "https://openapi.naver.com/v1/search/book.json?" +
                "query=" + URLEncoder.encode(title, "UTF-8") +
                "&display=" + 20 +
                "&sort=sim";

        // 요청 헤더 설정
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        // API 호출
        String responseBody = get(apiUrl, requestHeaders);
        log.info("API 응답: {}", responseBody);

        // 응답 JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        BookDTO naverBookResponse = objectMapper.readValue(responseBody, BookDTO.class);

        // 응답에서 첫 번째 책 정보를 반환
        if (naverBookResponse != null && naverBookResponse.getItems() != null && !naverBookResponse.getItems().isEmpty()) {
            return naverBookResponse;
        } else {
            log.warn("검색된 책 정보가 없습니다. Title: {}", title);
            return null;
        }
    }
    /**
     * 주어진 API URL과 헤더를 사용하여 GET 요청을 보내고 응답을 반환하는 메서드
     * @param apiUrl API 요청 URL
     * @param requestHeaders API 요청 헤더
     * @return API 응답 본문
     */
    private static String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            log.info("API 응답 코드: {}", responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                String errorResponse = readBody(con.getErrorStream());
                log.error("API 오류 응답: {}", errorResponse);
                throw new RuntimeException("API 요청 오류: " + errorResponse);
            }
        } catch (IOException e) {
            log.error("API 요청과 응답 실패", e);
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    /**
     * 주어진 API URL로 연결을 설정하는 메서드
     * @param apiUrl 연결할 API URL
     * @return HttpURLConnection 객체
     */
    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            log.error("API URL이 잘못되었습니다. : {}", apiUrl, e);
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            log.error("연결이 실패했습니다. : {}", apiUrl, e);
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    /**
     * 주어진 InputStream을 읽어 문자열로 반환하는 메서드
     * @param body InputStream 객체
     * @return 읽어들인 문자열
     */
    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            log.error("API 응답을 읽는 데 실패했습니다.", e);
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }
}
