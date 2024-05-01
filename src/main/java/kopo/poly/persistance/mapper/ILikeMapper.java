package kopo.poly.persistance.mapper;

import kopo.poly.dto.LikeDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ILikeMapper {

    int getLikeCount(LikeDTO pDTO) throws Exception; // 좋아요 정보 조회

    int getLike(LikeDTO pDTO) throws Exception; // 좋아요 정보 조회

    void insertLike(LikeDTO pDTO) throws Exception; // 좋아요 정보 등록

    void deleteLike(LikeDTO pDTO) throws Exception; // 좋아요 정보 삭제

}
