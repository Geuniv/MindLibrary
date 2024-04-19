package kopo.poly.persistance.mapper;

import kopo.poly.dto.BoardDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IBoardMapper {

    // 커뮤니티 리스트
    List<BoardDTO> getBoardList() throws Exception;

    // 커뮤니티 글 등록
    void insertBoardInfo(BoardDTO pDTO) throws Exception;

    // 커뮤니티 상세보기
    BoardDTO getBoardInfo(BoardDTO pDTO) throws Exception;

    // 커뮤니티 조회수 업데이트
    void updateBoardReadCnt(BoardDTO pDTO) throws Exception;

    // 커뮤니티 글 수정
    void updateBoardInfo(BoardDTO pDTO) throws Exception;

    // 커뮤니티 글 삭제
    void deleteBoardInfo(BoardDTO pDTO) throws Exception;
}
