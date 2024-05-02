package kopo.poly.service;

import kopo.poly.dto.LikeDTO;
import kopo.poly.dto.BoardDTO;

import java.util.List;
import java.util.Map;

public interface ILikeService {
    int getLikeCount(LikeDTO pDTO) throws Exception; // 좋아요 정보 조회
    int getLike(LikeDTO pDTO) throws Exception; // 좋아요 정보 조회
    void insertLike(LikeDTO pDTO) throws Exception; // 좋아요 정보 등록
    void deleteLike(LikeDTO pDTO) throws Exception; // 좋아요 정보 삭제

}