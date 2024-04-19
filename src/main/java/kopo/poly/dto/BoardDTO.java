package kopo.poly.dto;

import lombok.Builder;

@Builder
public record BoardDTO(

        String boardSeq,

        String userId,

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
