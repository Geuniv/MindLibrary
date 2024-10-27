package kopo.poly.service.impl;

import kopo.poly.dto.CalendarDTO;
import kopo.poly.persistance.mapper.ICalendarMapper;
import kopo.poly.service.ICalendarService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CalendarService implements ICalendarService {

    private final ICalendarMapper calendarMapper;

    /* 일정 추가 */
    @Override
    public int insertCalendar(CalendarDTO pDTO) throws Exception {

        log.info(".service 일정 추가 시작");

        int res = 0;
        int success = calendarMapper.insertCalendar(pDTO);

        if (success > 0) {
            res = 1;

        }

        return res;
    }

    /* 일정 리스트 */
    @Override
    public List<CalendarDTO> getCalendarList(CalendarDTO pDTO) throws Exception {

        log.info(".service 일정 리스트 실행");

        String userId = CmmUtil.nvl(pDTO.getUserId());
        String start = CmmUtil.nvl(pDTO.getStart());
        String end = CmmUtil.nvl(pDTO.getEnd());

        log.info("userId : " + userId);
        log.info("start : " + start);
        log.info("end : " + end);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("start", start);
        paramMap.put("end", end);

        log.info(".service 일정 리스트 종료");

        return calendarMapper.getCalendarList(userId, start, end);
    }

    /* 오늘 일정 리스트 */
    @Override
    public List<CalendarDTO> getTodayCalendarList(CalendarDTO pDTO) throws Exception {

        log.info(".service 오늘 일정 리스트 실행");

        log.info(".service 오늘 일정 리스트 종료");
        return calendarMapper.getTodayCalendarList(pDTO);
    }

    /* 일정 조회 */
    @Transactional
    @Override
    public CalendarDTO getCalendarInfo(CalendarDTO pDTO) throws Exception {

        log.info(".service 일정 조회 시작");

        log.info(".service 일정 조회 종료");

        return calendarMapper.calendarInfo(pDTO);
    }

    /* 일정 수정 */
    @Override
    public void updateCalendar(CalendarDTO pDTO) throws Exception {

        log.info(".service 일정 수정 시작");

        log.info(".service 일정 수정 종료");

        calendarMapper.updateCalendar(pDTO);

    }

    /* 일정 삭제 */
    @Override
    public void deleteCalendar(CalendarDTO pDTO) throws Exception {

        log.info(".service 일정 삭제 시작");

        log.info(".service 일정 삭제 종료");

        calendarMapper.deleteCalendar(pDTO);

    }

    /* 일정 확인 */
    @Override
    public List<CalendarDTO> checkCalendar(CalendarDTO pDTO) throws Exception {

        log.info(".service 일정 가져오기 실행");

        log.info(".service 일정 가져오기 종료");
        return calendarMapper.checkCalendar(pDTO);
    }
}
