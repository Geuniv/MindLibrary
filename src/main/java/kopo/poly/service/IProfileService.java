package kopo.poly.service;

import kopo.poly.dto.UserInfoDTO;

public interface IProfileService {

    UserInfoDTO getProfile(UserInfoDTO pDTO) throws Exception;

    void updateProfile(UserInfoDTO pDTO) throws Exception;

    void deleteUserInfo(UserInfoDTO pDTO) throws Exception;
}
