package kopo.poly.persistance.mapper;

import kopo.poly.dto.BoardDTO;
import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IBoardMapper {

    // 커뮤니티 리스트
    List<Map<String, Object>> getBoardList() throws Exception; // 게시판 목록 조회

    // 커뮤니티 글 등록
    void insertBoardInfo(BoardDTO pDTO) throws Exception;

    // 커뮤니티 상세보기
    Map<String, Object> getBoardInfo(BoardDTO pDTO) throws Exception; // 게시글 정보 조회

    // 커뮤니티 조회수 업데이트
    void updateBoardReadCnt(BoardDTO pDTO) throws Exception;

    // 커뮤니티 글 수정
    void updateBoardInfo(BoardDTO pDTO) throws Exception;

    // 커뮤니티 글 삭제
    void deleteBoardInfo(BoardDTO pDTO) throws Exception;

    /* 회원 탈퇴 */
    void deleteUserInfo(UserInfoDTO pDTO) throws Exception;

}
