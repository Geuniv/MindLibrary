package kopo.poly.service;

import kopo.poly.dto.FileDTO;
import kopo.poly.dto.UserInfoDTO;

import java.util.List;

public interface IFileService {

    /* 경로 가져오기 */
    List<FileDTO> getFile(UserInfoDTO pDTO) throws Exception;

    /* 파일 저장 */
    void insertFile(FileDTO pDTO) throws Exception;

    /* 파일 수정 */
    void updateFile(UserInfoDTO pDTO) throws Exception;

    /* 파일 삭제 */
    void deleteFile(UserInfoDTO pDTO) throws Exception;

}
