package kopo.poly.service.impl;

import kopo.poly.dto.BoardDTO;
import kopo.poly.persistance.mapper.IBoardMapper;
import kopo.poly.service.IBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService implements IBoardService {

    private final IBoardMapper boardMapper;

    @Override
    public List<BoardDTO> getBoardList() throws Exception {

        log.info(this.getClass().getName() + ".getBoardList Start!!");

        return boardMapper.getBoardList();
    }

    @Transactional
    @Override
    public BoardDTO getBoardInfo(BoardDTO pDTO, boolean type) throws Exception {

        log.info(this.getClass().getName() + ".getBoardInfo Start!!");

        // 상세보기할 때마다, 조회수 증가하기(수정보기는 제외)
        if (type) {
            log.info("Update ReadCNT");
            boardMapper.updateBoardReadCnt(pDTO);
        }
        return boardMapper.getBoardInfo(pDTO);
    }

    @Transactional
    @Override
    public void insertBoardInfo(BoardDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertBoardInfo Start!!");

        boardMapper.insertBoardInfo(pDTO);

    }

    @Transactional
    @Override
    public void updateBoardInfo(BoardDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateBoardInfo Start!!");

        boardMapper.updateBoardInfo(pDTO);

    }

    @Transactional
    @Override
    public void deleteBoardInfo(BoardDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteBoardInfo Start!!");

        boardMapper.deleteBoardInfo(pDTO);

    }
}
