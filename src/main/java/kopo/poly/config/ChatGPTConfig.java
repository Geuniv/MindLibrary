package kopo.poly.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * ChatGPT API를 사용하기 위한 환경 구성 클래스
 *
 * 이 클래스는 스프링 부트 애플리케이션에서 RestTemplate과 HttpHeaders를 빈으로 등록합니다.
 * RestTemplate은 RESTful 웹 서비스와 통신하기 위한 클라이언트이고,
 * HttpHeaders는 HTTP 요청 헤더를 설정하기 위한 객체입니다.
 *
 * <p>이 클래스는 다음과 같은 두 개의 빈을 정의합니다:</p>
 * <ul>
 *     <li>{@link RestTemplate}: RESTful 서비스와 통신하는 클라이언트</li>
 *     <li>{@link HttpHeaders}: HTTP 요청 헤더를 설정하는 객체로, 여기서는 OpenAI API 호출에 사용됩니다.</li>
 * </ul>
 *
 * <p>이 클래스는 @Configuration 어노테이션으로 마킹되어 있으며, 스프링 부트 애플리케이션이 시작될 때 자동으로 빈을 생성합니다.</p>
 *
 * <p>secretKey 필드는 application.properties 파일에서 설정된 OpenAI API 키를 주입받습니다.</p>
 *
 * @author : lee
 * @fileName : ChatGPTConfig
 * @since : 01/18/24
 */
@Configuration
public class ChatGPTConfig {

    // application.properties 파일에서 'openai.secret-key' 속성 값을 주입받는 변수
    @Value("${openai.secret-key}")
    private String secretKey;

    /**
     * RestTemplate 빈을 생성합니다.
     *
     * RestTemplate은 스프링에서 제공하는 HTTP 요청/응답을 처리하는 클라이언트입니다.
     * 이 빈은 ChatGPT API와의 통신에 사용됩니다.
     *
     * @return RestTemplate 객체
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * HttpHeaders 빈을 생성합니다.
     *
     * HttpHeaders는 HTTP 요청의 헤더를 설정하는 객체입니다.
     * 이 메서드는 Bearer 인증 헤더와 Content-Type을 JSON으로 설정한 HttpHeaders 객체를 생성하여 반환합니다.
     *
     * @return 설정된 HttpHeaders 객체
     */
    @Bean
    public HttpHeaders httpHeaders() {
        HttpHeaders headers = new HttpHeaders();

        // Bearer 인증 헤더를 설정합니다. 이는 OpenAI API에 접근하기 위해 필요합니다.
        headers.setBearerAuth(secretKey);

        // Content-Type을 JSON으로 설정합니다. 이는 API 요청 시 JSON 형식의 데이터를 전송하기 위해 필요합니다.
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
