package kopo.poly.service.impl;

import kopo.poly.dto.CommentDTO;
import kopo.poly.persistance.mapper.ICommentMapper;
import kopo.poly.service.ICommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService implements ICommentService {

    private final ICommentMapper commentMapper;

    @Override
    public List<CommentDTO> getCommentList(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + "getCommentList Start!!");

        return commentMapper.getCommentList(pDTO);
    }

    @Transactional
    @Override
    public void insertCommentInfo(CommentDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".insertCommentInfo Start!!");

        commentMapper.insertCommentInfo(pDTO);
    }

    @Transactional
    @Override
    public void deleteCommentInfo(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteCommentInfo Start!!");

        commentMapper.deleteCommentInfo(pDTO);
    }

}