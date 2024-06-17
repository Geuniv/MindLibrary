package kopo.poly.service.impl;

import kopo.poly.dto.BoardDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.IBoardMapper;
import kopo.poly.service.IBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService implements IBoardService {

    private final IBoardMapper boardMapper;

    /* 게시글 목록 조회 코드 */
    @Override
    public List<Map<String, Object>> getBoardList() throws Exception {

        log.info(this.getClass().getName() + ".getBoardList start!");

        return boardMapper.getBoardList();
    }

    @Transactional
    @Override
    public BoardDTO getBoardInfo(BoardDTO pDTO, boolean type) throws Exception {

        log.info(this.getClass().getName() + ".getBoardInfo Start!");

        log.info("Update ReadCNT");

        boardMapper.updateBoardReadCnt(pDTO);

        Map<String, Object> rMap = boardMapper.getBoardInfo(pDTO);

        BoardDTO rDTO = BoardDTO.builder().boardSeq(String.valueOf(rMap.get("boardSeq"))
        ).notification(String.valueOf(rMap.get("notification"))
        ).boardTitle(String.valueOf(rMap.get("boardTitle"))
        ).boardContent(String.valueOf(rMap.get("boardContent"))
        ).userId(String.valueOf(rMap.get("userId"))
        ).userNickname(String.valueOf(rMap.get("userNickname"))
        ).boardViews(String.valueOf(rMap.get("boardViews"))
        ).boardRegDt(String.valueOf(rMap.get("boardRegDt"))
        ).boardRegId(String.valueOf(rMap.get("boardRegId"))
        ).build();

        log.info(this.getClass().getName() + ".getBoardInfo End!");

        return rDTO;

    }

    @Transactional
    @Override
    public int insertBoardInfo(BoardDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertBoardInfo Start!!");

        log.info(this.getClass().getName() + ".insertBoardInfo End!!");

        return boardMapper.insertBoardInfo(pDTO);
    }

    @Override
    public List<BoardDTO> getPostsByUserId(String userId) throws Exception {
        return boardMapper.getPostsByUserId(userId);
    }

    /* 커뮤니티 순번 가져오기 */
    @Override
    public String getNextBoardSeq() throws Exception {
        log.info(".service 커뮤니티 순번 가져오기 실행");

        int nextSeq = boardMapper.getNextBoardSeq();

        return String.valueOf(nextSeq);
    }

    @Transactional
    @Override
    public void updateBoardInfo(BoardDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateBoardInfo Start!!");

        boardMapper.updateBoardInfo(pDTO);

        log.info(this.getClass().getName() + ".updateBoardInfo End!!");

    }

    @Transactional
    @Override
    public void deleteBoardInfo(BoardDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteBoardInfo Start!!");

        boardMapper.deleteBoardInfo(pDTO);

        log.info(this.getClass().getName() + ".deleteBoardInfo End!!");

    }

    @Override
    public void deleteUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteUserInfo Start!");

        boardMapper.deleteUserInfo(pDTO);

        log.info(this.getClass().getName() + ".deleteUserInfo End!");
    }
}
