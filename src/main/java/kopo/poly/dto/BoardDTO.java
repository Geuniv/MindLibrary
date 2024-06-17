package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record BoardDTO(

        String boardSeq, // 기본키, 순번

        String userId, // 유저 아이디

        String userNickname, // 닉네임

        String notification, // 공지글 여부

        String boardTitle, // 제목

        String boardContent, // 내용

        String boardViews, // 조회수

        String boardLike, // 좋아요

        String boardRegId, // 작성자

        String boardRegDt, // 작성일

        String boardChgId, // 수정자

        String boardChgDt,// 수정일

        String fileName // 파일 게시글 수정창에서 삭제하기 위한 파라미터
) {
}
