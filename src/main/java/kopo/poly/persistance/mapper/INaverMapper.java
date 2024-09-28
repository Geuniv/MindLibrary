package kopo.poly.persistance.mapper;

import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface INaverMapper {

    // DB에서 네이버로 가입한 회원의 정보를 가져오기
    UserInfoDTO getUserInfoById(UserInfoDTO pDTO) throws Exception;

}
