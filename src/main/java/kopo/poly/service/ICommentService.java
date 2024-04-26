package kopo.poly.service;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.BoardDTO;

import java.util.List;

public interface ICommentService {

    /**댓글 리스트*/

    List<CommentDTO> getCommentList(CommentDTO pDTO) throws Exception;

    /**댓글 등록*/
    void insertCommentInfo(CommentDTO pDTO) throws Exception;

    /**댓글 삭제*/
    void deleteCommentInfo(CommentDTO pDTO) throws Exception;
}