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
public class TestDTO {

    private String testSeq; // 자가진단 순번
    
    private String userId; // 유저아이디

    private String userTestResult; // 자가진단 결과
    
    private String TestRegDt; // 자가진단 실행일시
}
