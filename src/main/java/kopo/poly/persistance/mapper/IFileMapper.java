package kopo.poly.persistance.mapper;

import kopo.poly.dto.FileDTO;
import kopo.poly.dto.UserInfoDTO;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IFileMapper {

    /* 경로 가져오기 */
    List<FileDTO> getFile(UserInfoDTO pDTO) throws Exception;

    /* 파일 저장 */
    void insertFile(FileDTO pDTO) throws Exception;

    /* 이미지 수정*/
    void updateFile(UserInfoDTO pDTO) throws Exception;

    /* 이미지 삭제 */
    void deleteFile(UserInfoDTO pDTO) throws Exception;

}
