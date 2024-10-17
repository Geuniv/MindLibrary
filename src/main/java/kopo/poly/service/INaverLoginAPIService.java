package kopo.poly.service;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import kopo.poly.config.OpenFeignConfig;
import kopo.poly.dto.NaverDTO;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "naverLoginClient", url = "https://openapi.naver.com", configuration = OpenFeignConfig.class)
public interface INaverLoginAPIService {

    // 네이버 로그인 API ( Open Feign ) - 2024.10.05
    @RequestLine("POST /v1/nid/me")
    @Headers({"Authorization: Bearer {accessToken}", "Content-Type: application/x-www-form-urlencoded;charset=utf-8"})
    NaverDTO getUserInfo(
            @Param("accessToken") String accessToken

    );
}
