package kopo.poly.service;

import kopo.poly.dto.CheckDTO;
import kopo.poly.dto.UserInfoDTO;

public interface ICheckService {

    // 회원 가입하기(회원정보 등록하기)
    void insertCheckInfo(CheckDTO pDTO) throws Exception;

    CheckDTO getCheckInfo(CheckDTO pDTO) throws Exception;
}
