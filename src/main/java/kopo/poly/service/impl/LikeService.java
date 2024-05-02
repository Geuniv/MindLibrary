package kopo.poly.service.impl;

import kopo.poly.dto.LikeDTO;
import kopo.poly.dto.BoardDTO;
import kopo.poly.persistance.mapper.ILikeMapper;
import kopo.poly.persistance.mapper.IBoardMapper;
import kopo.poly.service.ILikeService;
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
public class LikeService implements ILikeService {

    private final ILikeMapper likeMapper;

    /*  좋아요 개수 등록 코드 */
    @Override
    public int getLikeCount(LikeDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getLikeCount start!");

        return likeMapper.getLikeCount(pDTO);
    }

    /*  좋아요 정보 등록 코드 */
    @Transactional
    @Override
    public int getLike(LikeDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getLike start!");

        return likeMapper.getLike(pDTO);
    }

    /*  좋아요 정보 등록 코드 */
    @Transactional
    @Override
    public void insertLike(LikeDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertLike start!");

        likeMapper.insertLike(pDTO);
    }

    /* 좋아요 정보 삭제 코드 */
    @Override
    public void deleteLike(LikeDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteLike start!");

        likeMapper.deleteLike(pDTO);
    }

}