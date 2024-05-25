package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CheckDTO {

    // 마음체크 순번
    private String checkSeq;

    // 유저 아이디
    private String userId;

    // 유저 질문
    private String userQuestion;

    // GPT 답변
    private String userAnswer;
}
