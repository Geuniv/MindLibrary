package kopo.poly.config;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import feign.Contract;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

@Configuration
public class OpenFeignConfig {

    // 네이버 클라이언트 ID
    @Value("${naver.client_id}")
    private String clientId;

    // 네이버 클라이언트 Secret
    @Value("${naver.client_secret}")
    private String clientSecret;

    // application/x-www-form-urlencoded 형식으로 인코딩을 위한 FormEncoder 설정 ( 네이버 로그인 API - 2024.10.05 )
    @Bean
    public Encoder feignFormEncoder() {
        return new FormEncoder(new SpringEncoder(() -> new HttpMessageConverters()));
    }

    // XML 응답 처리를 위한 SpringDecoder 설정 ( 네이버 로그인 API - 2024.10.05 )
    @Bean
    public Decoder feignDecoder() {
        return new SpringDecoder(() ->
                new HttpMessageConverters(
                        new MappingJackson2XmlHttpMessageConverter(new XmlMapper())));
    }

    // API 접속을 위해 접속 방법은 기본 값으로 설정함 ( 반드시 설정되어야 함 )
    @Bean
    public Contract feignContract() {
        return new Contract.Default();
    }

    /**
     * 네이버 API 호출에 사용되는 X-Naver-Client-Id, X-Naver-Client-Secret 설정
     * OpenFeign 통해 호출되는 모든 API 헤더에 적용됨
     */
    // 단 해당 로직의 경우 네이버 API에서 가져오는 것이 전부 헤더값이 달리기 때문에
    // 다른 종류의 API에도 들어가버림
    @Bean
    public RequestInterceptor requestInterceptor() {

        return requestTemplate -> {
            requestTemplate.header("X-Naver-Client-Id", clientId);
            requestTemplate.header("X-Naver-Client-Secret", clientSecret);
        };
    }

    @Bean
    Logger.Level feignLoggerLevel() {

        /*
        OpenFeign 통해 전송 및 전달받는 모든 과정에 대해 로그 찍기 설정

        NONE: 로깅하지 않음(기본값)
        BASIC: 요청 메소드와 URI와 응답 상태와 실행시간 로깅함
        HEADERS: 요청과 응답 헤더와 함께 기본 정보들을 남김
        FULL: 요청과 응답에 대한 헤더와 바디, 메타 데이터를 남김
        */
        return Logger.Level.FULL;
    }
}
