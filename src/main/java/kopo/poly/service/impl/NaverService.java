package kopo.poly.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.NaverDTO;
import kopo.poly.dto.TokenDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.IUserInfoMapper;
import kopo.poly.service.INaverAuthAPIService;
import kopo.poly.service.INaverLoginAPIService;
import kopo.poly.service.INaverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class NaverService implements INaverService {

    // Naver Login API ( OpenFeign 적용 ) - 2024.10.05

    private final IUserInfoMapper userInfoMapper;
    private final RestTemplate restTemplate;

    @Value("${naver.client_id}")
    private String naverClientId;

    @Value("${naver.client_secret}")
    private String naverClientSecret;

    @Value("${naver.redirect_uri}")
    private String naverRedirectUri;

    private final INaverAuthAPIService naverAuthAPIService;

    private final INaverLoginAPIService naverLoginAPIService;

    /* 토큰 가져오기 */
    @Override
    public TokenDTO getAccessToken(String code) throws Exception {

        log.info(".service 네이버 토큰 가져오기 실행");

        return naverAuthAPIService.getAccessToken(
                "authorization_code",
                naverClientId,
                naverClientSecret,
                naverRedirectUri,
                code
        );
    }

    /* 네이버에서 정보 가져오기 */
    @Override
    public NaverDTO getNaverUserInfo(TokenDTO pDTO) throws Exception {

        log.info(".service 네이버에서 유저 정보 가져오기 실행");

        return naverLoginAPIService.getUserInfo(pDTO.getAccess_token());
    }

    /* DB에서 네이버로 가입한 회원의 정보를 가져오기 */
    @Override
    public UserInfoDTO getUserInfoById(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserInfoById 시작!");

        UserInfoDTO rDTO = userInfoMapper.getUserInfoById(pDTO);

        log.info(this.getClass().getName() + ".getUserInfoById 끝!");

        return rDTO;
    }

    @Override
    public String revokeNaverAccessToken(String accessToken) {
        log.info("네이버 연동 해제 요청 시작! Access Token: " + accessToken);

        try {
            // 네이버 연동 해제 API 호출
            String response = naverAuthAPIService.revokeAccessToken(
                    "delete",                  // grant_type: delete
                    naverClientId,             // 네이버 client ID
                    naverClientSecret,         // 네이버 client Secret
                    accessToken,               // 사용자의 access token
                    "NAVER"                    // service provider: NAVER
            );

            log.info("네이버 연동 해제 응답: " + response);
            return "success";
        } catch (Exception e) {
            log.error("네이버 연동 해제 실패: " + e.getMessage());
            return "failure";
        }
    }

//    @Override
//    public TokenDTO getAccessToken(String code) throws Exception {
//
//
//        log.info(".service 네이버 토큰 가져오기 실행");
//
//        /*
//         * POST 방식으로 key=value 형식으로 데이터 요청(네이버 쪽으로)
//         * http 요청을 편하게 할수 있게하는 Retrofit2, OkHttp, RestTemplate 라이브러리가 있다
//         */
//        RestTemplate rt = new RestTemplate();
//
//        // HttpHeader 오브젝트 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        // HttpBody 오브젝트 생성
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", naverClientId);
//        params.add("client_secret", naverClientSecret);
//        params.add("redirect_uri", naverRedirectUri);
//        params.add("code", code);
//
//        log.info("code : " + code);
//
//        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
//        HttpEntity<MultiValueMap<String, String>> naverTokenRequest =
//                new HttpEntity<>(params, headers);
//
//        // Http 요청하기 - POST 방식으로 - 그리고 response 변수의 응답 받음.
//        ResponseEntity<String> response = rt.exchange(
//                "https://nid.naver.com/oauth2.0/token",
//                HttpMethod.POST,
//                naverTokenRequest,
//                String.class
//        );
//
//        // 객체에 담아볼 것이다. Gson, Json Simple, ObjectMapper 라이브러리가 있다.
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        TokenDTO tokenDTO = null;
//
//        try {
//            tokenDTO = objectMapper.readValue(response.getBody(), TokenDTO.class);
//
//        } catch (JsonMappingException e) {
//            log.info(e.toString());
//            e.printStackTrace();    // Exception 발생 이유와 위치는 어디에서 발생했는지 전체적인 단계 출력
//
//        } catch (JsonProcessingException e) {
//            log.info(e.toString());
//            e.printStackTrace();    // Exception 발생 이유와 위치는 어디에서 발생했는지 전체적인 단계 출력
//
//        }
//
//        log.info(".service 네이버 토큰 가져오기 종료");
//
//        return tokenDTO;
//    }
//
//    /* 네이버에서 정보 가져오기 */
//    @Override
//    public NaverDTO getNaverUserInfo(TokenDTO pDTO) throws Exception {
//
//        log.info(".service 네이버에서 유저 정보 가져오기 실행");
//
//        log.info("TokenDTO pDTO : " + pDTO);
//
//        // http 요청을 편하게 할 수 있다. Retrofit2, OkHttp, RestTemplate 라이브러리가 있다.
//        RestTemplate rt = new RestTemplate();
//
//        // HttpHeader 오브젝트 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + pDTO.getAccess_token());
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        log.info("headers : " + headers);
//
//        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
//        HttpEntity<MultiValueMap<String, String>> naverProfileRequest = new HttpEntity<>(headers);
//
//        log.info("naverProfileRequest : " + naverProfileRequest);
//
//        // Http 요청하기 - POST방식으로 - 그리고 response 변수의 응답 받음.
//        ResponseEntity<String> response2 = rt.exchange(
//                "https://openapi.naver.com/v1/nid/me",
//                HttpMethod.POST,
//                naverProfileRequest,
//                String.class
//        );
//
//        log.info("response2 : " + response2);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        NaverDTO naverDTO = null;
//
//        try {
//            naverDTO = objectMapper.readValue(response2.getBody(), NaverDTO.class);
//
//            log.info("naverDTO : " + naverDTO);
//
//            /*  실패 사유 확인용 로그  */
//        } catch (JsonMappingException e) {
//            log.info(e.toString());
//            e.printStackTrace();    // Exception 발생 이유와 위치는 어디에서 발생했는지 전체적인 단계 출력
//
//        } catch (JsonProcessingException e) {
//            log.info(e.toString());
//            e.printStackTrace();    // Exception 발생 이유와 위치는 어디에서 발생했는지 전체적인 단계 출력
//
//        }
//
//        return naverDTO;
//    }

}