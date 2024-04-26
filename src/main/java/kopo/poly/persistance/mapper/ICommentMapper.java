package kopo.poly.persistance.mapper;

import kopo.poly.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ICommentMapper {

    // 댓글 리스트
    List<CommentDTO> getCommentList(CommentDTO pDTO) throws Exception;

    // 댓글 등록
    void insertCommentInfo(CommentDTO pDTO) throws Exception;

    // 댓글 삭제
    void deleteCommentInfo(CommentDTO pDTO) throws Exception;

//    void updateComment(CommentDTO pDTO) throws Exception; // 댓글 수정

}
