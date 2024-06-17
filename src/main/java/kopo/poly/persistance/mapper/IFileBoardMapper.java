package kopo.poly.persistance.mapper;

import kopo.poly.dto.BoardDTO;
import kopo.poly.dto.FileDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IFileBoardMapper {

    /* 게시글 경로 가져오기 */
    List<FileDTO> getBoardFile(BoardDTO pDTO) throws Exception;

    /* 게시글 파일 저장 */
    void insertBoardFile(FileDTO pDTO) throws Exception;

    /* 게시글 장수 가져오기 */
    FileDTO getCountPage(BoardDTO pDTO) throws Exception;

    /* 게시글 이미지 수정*/
    void updateBoardFile(BoardDTO pDTO) throws Exception;

    /* 게시글 이미지 삭제 */
    void deleteBoardFile(BoardDTO pDTO) throws Exception;
}
