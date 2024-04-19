package kopo.poly.persistance.mapper;

import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IProfileMapper {

    UserInfoDTO getProfile(UserInfoDTO pDTO) throws Exception;

    void updateProfile(UserInfoDTO pDTO) throws Exception;

    void deleteUserInfo(UserInfoDTO pDTO) throws Exception;
}
