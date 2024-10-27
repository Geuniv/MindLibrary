package kopo.poly.service;

import kopo.poly.dto.CalendarDTO;

import java.util.List;

public interface ICalendarService {

    /* 일정 추가 */
    int insertCalendar(CalendarDTO pDTO) throws Exception;

    /* 일정 리스트 */
    List<CalendarDTO> getCalendarList(CalendarDTO pDTO) throws Exception;

    /* 오늘 일정 리스트 */
    List<CalendarDTO> getTodayCalendarList(CalendarDTO pDTO) throws Exception;

    /* 일정 조회 */
    CalendarDTO getCalendarInfo (CalendarDTO pDTO) throws Exception;

    /* 일정 수정 */
    void updateCalendar(CalendarDTO pDTO) throws Exception;

    /* 일정 삭제 */
    void deleteCalendar(CalendarDTO pDTO) throws Exception;

    /* 일정 확인 */
    List<CalendarDTO> checkCalendar(CalendarDTO pDTO) throws Exception;
}
