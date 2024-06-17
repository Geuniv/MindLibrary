package kopo.poly.service.impl;

import kopo.poly.dto.BoardDTO;
import kopo.poly.dto.FileDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.IFileBoardMapper;
import kopo.poly.persistance.mapper.IFileMapper;
import kopo.poly.service.IFileBoardService;
import kopo.poly.service.IFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileBoardService implements IFileBoardService {

    private final IFileBoardMapper fileBoardMapper;

    /* 게시글 파일 경로 가져오기 */
    @Override
    public List<FileDTO> getBoardFile(BoardDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 파일 가져오기 ( 게시글 ) 시작!");

        return fileBoardMapper.getBoardFile(pDTO);

    }

    /* 게시글 파일 저장하기 */
    @Override
    public void insertBoardFile(FileDTO pDTO) throws Exception {

        log.info(this.getClass().getClass() + ".service 파일 저장하기 ( 게시글 ) 시작!");

        fileBoardMapper.insertBoardFile(pDTO);

    }

    /* 장수 가져오기 */
    @Override
    public FileDTO getCountPage(BoardDTO pDTO) throws Exception {

        log.info(".service 이미지 장수 가져오기 ( 게시글 ) 실행");

        return fileBoardMapper.getCountPage(pDTO);
    }

    /* 게시글 파일 수정하기 */
    @Override
    public void updateBoardFile(BoardDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 파일 업데이트 ( 게시글 ) 시작!");

        fileBoardMapper.updateBoardFile(pDTO);

    }

    /* 게시글 파일 삭제하기 */
    @Override
    public void deleteBoardFile(BoardDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 파일 삭제 ( 게시글 ) 시작!");

        fileBoardMapper.deleteBoardFile(pDTO);

    }
}
