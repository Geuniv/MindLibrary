package kopo.poly.service;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import kopo.poly.config.OpenFeignConfig;
import kopo.poly.dto.TokenDTO;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "naverAuthClient", url = "https://nid.naver.com", configuration = OpenFeignConfig.class)
public interface INaverAuthAPIService {

    // 네이버 로그인 API 회원가입 ( Open Feign ) - 2024.10.05
    @RequestLine("POST /oauth2.0/token")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    TokenDTO getAccessToken(
            @Param("grant_type") String grantType,
            @Param("client_id") String clientId,
            @Param("client_secret") String clientSecret,
            @Param("redirect_uri") String redirectUri,
            @Param("code") String code
    );

    // 네이버 연동 해제 API 메서드 추가
    @RequestLine("POST /oauth2.0/token")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    String revokeAccessToken(
            @Param("grant_type") String grantType,
            @Param("client_id") String clientId,
            @Param("client_secret") String clientSecret,
            @Param("access_token") String accessToken,
            @Param("service_provider") String serviceProvider
    );
}
