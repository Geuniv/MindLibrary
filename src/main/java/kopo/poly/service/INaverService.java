package kopo.poly.service;

import kopo.poly.dto.NaverDTO;
import kopo.poly.dto.TokenDTO;
import kopo.poly.dto.UserInfoDTO;

public interface INaverService {

    /* 토큰 가져오기 */
    TokenDTO getAccessToken(String code) throws Exception;

    /* 네이버에서 정보 가져오기 */
    NaverDTO getNaverUserInfo(TokenDTO pDTO) throws Exception;

    /* DB에서 네이버로 가입한 회원의 정보를 가져오기 */
    UserInfoDTO getUserInfoById(UserInfoDTO pDTO) throws Exception;

    /* 추가: 네이버 연동 해제 메서드 */
    String revokeNaverAccessToken(String accessToken);
}
