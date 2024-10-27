package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.CalendarDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.service.ICalendarService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "calendar")
@Controller
public class CalendarController {

    private final ICalendarService calendarService;

    @GetMapping(value = "calendar")
    public String calendar(ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".calendar Start!!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID")); // 아이디

        if (userId.length() < 1) {
            String msg = "로그인 정보가 없습니다. \n 로그인 후 이용해 주세요.";
            String url = "/user/login";

            model.addAttribute("msg", msg);
            model.addAttribute("url", url);

            return "redirect";
        }

        // 오늘 일정 가져오기
        CalendarDTO pDTO = new CalendarDTO();
        pDTO.setUserId(userId);
        List<CalendarDTO> rList = Optional.ofNullable(calendarService.getTodayCalendarList(pDTO)).orElseGet(ArrayList::new);

        model.addAttribute(rList);

        log.info(this.getClass().getName() + ".calendar End!!");

        return "calendar/calendar";
    }

    /* 일정 추가 */
    @PostMapping(value = "insertCalendar")
    @ResponseBody
    public MsgDTO insertCalendar(@RequestBody CalendarDTO pDTO, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".insertCalendar Start!");

//        MsgDTO msgDTO = new MsgDTO();
        MsgDTO msgDTO = MsgDTO.builder().build(); // 결과 메시지 구조
        int res = 0;
        String msg = "";  // 메시지 내용

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            if (userId == null || userId.isEmpty()) {
                msgDTO = msgDTO.builder().result(0).msg("로그인 후 이용할 수 있습니다.").build();
//                msgDTO.setResult(0);
//                msgDTO.setMsg("로그인 후 이용할 수 있습니다.");
                return msgDTO;
            }
            pDTO.setUserId(userId);

            log.info("calendarTitle : " + pDTO.getTitle());
            log.info("start : " + pDTO.getStart());
            log.info("end : " + pDTO.getEnd());
            log.info("startTime : " + pDTO.getStartTime());
            log.info("endTime : " + pDTO.getEndTime());
            log.info("calendarCon : " + pDTO.getContents());

            if (pDTO.getTitle() == null || pDTO.getTitle().isEmpty() ||
                    pDTO.getStart() == null || pDTO.getStart().isEmpty() ||
                    pDTO.getEnd() == null || pDTO.getEnd().isEmpty()) {
                msgDTO = msgDTO.builder().result(0).msg("Title, start date, and end date are required.").build();
//                msgDTO.setResult(0);
//                msgDTO.setMsg("Title, start date, and end date are required.");
                return msgDTO;
            }

            calendarService.insertCalendar(pDTO);

            msg = "일정이 추가되었습니다.";
            msgDTO = msgDTO.builder().result(1).msg(msg).build();
//            msgDTO.setResult(1);
//            msgDTO.setMsg(msg);

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.error(e.toString());
            e.printStackTrace();

            msgDTO = msgDTO.builder().result(0).msg(msg).build();
//            msgDTO.setResult(0);
//            msgDTO.setMsg(msg);

        } finally {
            log.info(this.getClass().getName() + ".insertCalendar End!");
        }
        return msgDTO;
    }





    /* 일정 리스트 */
    @ResponseBody
    @GetMapping(value = "calendarList")
    public List<Map<String, Object>> calendarList(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(".controller 일정 리스트 실행");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID")); // 아이디

        if (userId.isEmpty()) {
            log.info("User is not logged in.");
            return new ArrayList<>(); // 로그인 안된 경우 빈 리스트 반환
        }

        String start = CmmUtil.nvl(request.getParameter("start"));  // 시작 날짜
        String end = CmmUtil.nvl(request.getParameter("end"));  // 종료 날짜

        log.info("userId : " + userId);
        log.info("start : " + start);
        log.info("end : " + end);

        CalendarDTO pDTO = new CalendarDTO();
        pDTO.setUserId(userId);
        pDTO.setStart(start);
        pDTO.setEnd(end);

        // 일정 리스트 조회 서비스 호출하여 결과 받기
        List<CalendarDTO> rList = Optional.ofNullable(calendarService.getCalendarList(pDTO)).orElseGet(ArrayList::new);

        // 반환할 이벤트 리스트 생성
        List<Map<String, Object>> events = new ArrayList<>();
        // 조회된 일정 데이터를 Map 형태로 변환하여 리스트에 추가
        for (CalendarDTO dto : rList) {
            Map<String, Object> event = new HashMap<>();
            event.put("calendarSeq", dto.getCalendarSeq());
            event.put("title", dto.getTitle());
            event.put("start", dto.getStart());
            event.put("end", dto.getEnd());
            event.put("startTime", dto.getStartTime());
            event.put("endTime", dto.getEndTime());
            event.put("contents", dto.getContents());
            events.add(event);
        }

        log.info("rList size: " + rList.size());
        log.info(".controller 일정 리스트 종료");

        return events;
    }

    /* 일정 수정 */
    @ResponseBody
    @PostMapping(value = "calendarUpdate")
    public MsgDTO calendarUpdate(@RequestBody CalendarDTO pDTO, HttpSession session) {

        log.info(this.getClass().getName() + ".calendarUpdate Start!");

//        MsgDTO msgDTO = new MsgDTO();
        MsgDTO msgDTO = MsgDTO.builder().build(); // 결과 메시지 구조
        int res = 0;
        String msg = "";  // 메시지 내용

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            if (userId == null || userId.isEmpty()) {
                msgDTO = msgDTO.builder().result(0).msg("로그인 후 이용할 수 있습니다.").build();
//                msgDTO.setResult(0);
//                msgDTO.setMsg("로그인 후 이용할 수 있습니다.");
                return msgDTO;
            }
            pDTO.setUserId(userId);

            log.info("calendarSeq : " + pDTO.getCalendarSeq());
            log.info("calendarTitle : " + pDTO.getTitle());
            log.info("start : " + pDTO.getStart());
            log.info("end : " + pDTO.getEnd());
            log.info("startTime : " + pDTO.getStartTime());
            log.info("endTime : " + pDTO.getEndTime());
            log.info("calendarCon : " + pDTO.getContents());

            if (pDTO.getTitle() == null || pDTO.getTitle().isEmpty() ||
                    pDTO.getStart() == null || pDTO.getStart().isEmpty() ||
                    pDTO.getEnd() == null || pDTO.getEnd().isEmpty()) {
                msgDTO = msgDTO.builder().result(0).msg("Title, start date, and end date are required.").build();
//                msgDTO.setResult(0);
//                msgDTO.setMsg("Title, start date, and end date are required.");
                return msgDTO;
            }

            calendarService.updateCalendar(pDTO);

            msg = "일정이 수정되었습니다.";
            msgDTO = msgDTO.builder().result(1).msg(msg).build();
//            msgDTO.setResult(1);
//            msgDTO.setMsg(msg);

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.error(e.toString());
            e.printStackTrace();

            msgDTO = msgDTO.builder().result(0).msg(msg).build();
//            msgDTO.setResult(0);
//            msgDTO.setMsg(msg);

        } finally {
            log.info(this.getClass().getName() + ".calendarUpdate End!");
        }
        return msgDTO;
    }

    /* 일정 삭제 */
    @ResponseBody
    @PostMapping(value = "calendarDelete")
    public MsgDTO calendarDelete(@RequestBody CalendarDTO pDTO, HttpSession session) {
        log.info(this.getClass().getName() + ".calendarDelete Start!");

//        MsgDTO msgDTO = new MsgDTO();
        MsgDTO msgDTO = MsgDTO.builder().build(); // 결과 메시지 구조
        int res = 0;
        String msg = "";  // 메시지 내용

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            if (userId == null || userId.isEmpty()) {
                msgDTO = msgDTO.builder().result(0).msg("로그인 후 이용할 수 있습니다.").build();
//                msgDTO.setResult(0);
//                msgDTO.setMsg("로그인 후 이용할 수 있습니다.");
                return msgDTO;
            }
            pDTO.setUserId(userId);

            log.info("calendarSeq : " + pDTO.getCalendarSeq());

            calendarService.deleteCalendar(pDTO);

            msg = "삭제되었습니다.";
            msgDTO = msgDTO.builder().result(1).msg(msg).build();
//            msgDTO.setResult(1);
//            msgDTO.setMsg(msg);

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.error(e.toString());
            e.printStackTrace();

            msgDTO = msgDTO.builder().result(0).msg(msg).build();
//            msgDTO.setResult(0);
//            msgDTO.setMsg(msg);

        } finally {
            log.info(this.getClass().getName() + ".calendarDelete End!");
        }
        return msgDTO;
    }

    /* 일정 확인 */
    @ResponseBody
    @PostMapping(value = "checkCalendar")
    public List<CalendarDTO> checkCalendar(HttpServletRequest request, Model model, HttpSession session) throws Exception {

        log.info(".controller 일정 일자 가져오기 실행");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("userId : " + userId);

        CalendarDTO pDTO = new CalendarDTO();
        pDTO.setUserId(userId);

        List<CalendarDTO> sList = Optional.ofNullable(calendarService.checkCalendar(pDTO)).orElseGet(ArrayList::new);

        log.info("sList : " + sList.size());

        for (CalendarDTO dto : sList) {
            log.info(dto.getStart());
        }

        model.addAttribute("sList", sList);

        return sList;
    }
}
