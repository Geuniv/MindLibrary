package kopo.poly.service;

import kopo.poly.dto.CheckDTO;
import kopo.poly.dto.UserInfoDTO;

import java.util.List;

public interface ICheckService {

    // 회원 가입하기(회원정보 등록하기)
    void insertCheckInfo(CheckDTO pDTO) throws Exception;

    /* 마음체크 결과 조회 */
    CheckDTO getCheckInfo(CheckDTO pDTO) throws Exception;

    /* 사용자 ID로 마음체크 목록 조회 */
    List<CheckDTO> getCheckByUserId(CheckDTO pDTO) throws Exception;
}
