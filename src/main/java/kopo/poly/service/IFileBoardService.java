package kopo.poly.service;

import kopo.poly.dto.BoardDTO;
import kopo.poly.dto.FileDTO;
import kopo.poly.dto.UserInfoDTO;

import java.util.List;

public interface IFileBoardService {

    /* 게시글 경로 가져오기 */
    List<FileDTO> getBoardFile(BoardDTO pDTO) throws Exception;

    /* 게시글 파일 저장 */
    void insertBoardFile(FileDTO pDTO) throws Exception;

    /* 장수 가져오기 */
    FileDTO getCountPage(BoardDTO pDTO) throws Exception;

    /* 게시글 파일 수정 */
    void updateBoardFile(BoardDTO pDTO) throws Exception;

    /* 게시글 파일 삭제 */
    void deleteBoardFile(BoardDTO pDTO) throws Exception;

}
