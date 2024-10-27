package kopo.poly.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarDTO {


    private String userId; // 작성 아이디

    private String Start; // 시작일

    private String End; // 종료일

    private String startTime; // 시작 시간

    private String endTime; // 종료 시간

    //    private String regDt; // 등록일

    private String calendarSeq; // 순번

    private String title; // 제목

    private String contents; // 내용

}
