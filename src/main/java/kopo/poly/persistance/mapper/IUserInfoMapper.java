package kopo.poly.persistance.mapper;

import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserInfoMapper {

    // 회원 가입하기(회원정보 등록하기)
    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

    // 회원 가입 전 아이디 중복체크하기(DB조회하기)
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    // 회원 가입 전 이메일 중복체크하기(DB조회하기)
    UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception;

    // 회원 정보 수정 전 닉네임 중복체크하기(DB조회하기)
    UserInfoDTO getNicknameExists(UserInfoDTO pDTO) throws Exception;

    // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기
    UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception;

    /*
    아이디, 비밀번호 찾기에 활용
     1. 이름과 이메일이 맞다면, 아이디 알려주기
     2. 아이디, 이름과 이메일이 맞다면, 비밀번호 재설정하기
    */
    UserInfoDTO getUserId(UserInfoDTO pDTO) throws Exception;

    // 네이버 로그인 시 아이디만 받아서 DB 조회하기
    UserInfoDTO getUserInfoById(UserInfoDTO pDTO) throws Exception;

    // 비밀번호 재설정
    int updatePassword(UserInfoDTO pDTO) throws Exception;

    // 회원탈퇴
//    void deleteUser(UserInfoDTO pDTO) throws Exception;

}
