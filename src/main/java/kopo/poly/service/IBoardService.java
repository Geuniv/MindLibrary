package kopo.poly.service;

import kopo.poly.dto.BoardDTO;

import java.util.List;

public interface IBoardService {

    /**
     * 커뮤니티 리스트
     * @return 조회결과
     */
    List<BoardDTO> getBoardList() throws Exception;
    /**
     *  커뮤니티 상세 보기
     *  @param pDTO 상세내용 조회할 BoardSeq 값
     *  @param type 조회수 증가여부(수정보기는 조회수 증가하지 않음)
     *  @return 조회결과
     */

    BoardDTO getBoardInfo(BoardDTO pDTO, boolean type) throws Exception;
    /**
     * 커뮤니티 등록
     * @param pDTO 화면에서 입력된 커뮤니티 입력된 값들
     */
    void insertBoardInfo(BoardDTO pDTO) throws Exception;

    /**
     * 커뮤니티 수정
     * @param pDTO 화면에서 입력된 수정되기 위한 커뮤니티 입력된 값들
     */
    void updateBoardInfo(BoardDTO pDTO) throws Exception;

    /**
     * 공지사항 삭제
     * @param pDTO 삭제할 BoardSeq 값
     * */
    void deleteBoardInfo(BoardDTO pDTO) throws Exception;
}
