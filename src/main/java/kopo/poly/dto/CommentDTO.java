package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CommentDTO {

    private String commentSeq;

    private String boardSeq;

    private String userId;

    private String userNickname;

    private String commentContents;

    private String commentRegId;

    private String commentRegDt;

    private String commentChgId;

    private String commentChgDt;

    // 외래키들
    private String fileSeq;
}
