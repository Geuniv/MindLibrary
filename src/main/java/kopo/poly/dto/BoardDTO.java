package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record BoardDTO(

        String boardSeq,

        String userId,

        String userNickname,

        String notification,

        String boardTitle,

        String boardContent,

        String boardViews,

        String boardLike,

        String boardRegId,

        String boardRegDt,

        String boardChgId,

        String boardChgDt
) {
}
