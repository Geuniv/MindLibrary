package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.TestDTO;
import kopo.poly.service.ITestService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequestMapping(value = "/test")
@RequiredArgsConstructor
@Controller
public class TestController {

    private final ITestService testService;

    /**
     * 커뮤니티 작성 페이지 이동
     */
    @GetMapping("testReg")
    public String showSurvey(ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".controller 테스트 폼 시작 !");

        String ssUserId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("세션에 저장 되어있는 아이디('SS_USER_ID') : " + ssUserId);

        
        // 자가진단에 사용할 질문지 리스트
        List<String> questions = Arrays.asList(
                "평소에는 아무렇지도 않던 일들이 괴롭고 귀찮게 느껴졌다.",
                "나는 하루 중 아침에 가장 기분이 좋다.",
                "먹고 싶지 않고 식욕이 없다.",
                "어느 누가 도와준다 하더라도 나의 울적한 기분을 떨쳐 버릴 수 없을 것 같다.",
                "무슨 일을 하든 정신을 집중하기가 힘들었다.",
                "비교적 잘 지냈다.",
                "상당히 우울했다.",
                "모든 일들이 힘들게 느껴졌다.",
                "앞일이 암담하게 느껴졌다.",
                "지금까지의 내 인생은 실패작이라는 생각이 들었다.",
                "적어도 보통 사람들만큼의 능력은 있었다고 생각 한다.",
                "잠을 설쳤다.(잠을 잘 이루지 못했다.)",
                "두려움을 느꼈다.",
                "평소에 비해 말수가 적었다.",
                "세상에 홀로 있는 듯한 외로움을 느꼈다.",
                "큰 불만 없이 생활했다.",
                "사람들이 나에게 차갑게 대하는 것 같았다.",
                "갑자기 울음이 나왔다.",
                "마음이 슬펐다.",
                "사람들이 나를 싫어하는 것 같았다."
        );

        model.addAttribute("questions", questions);

        log.info(this.getClass().getName() + ".controller 테스트 폼 끝 !");

        return "test/testReg";
    }

    /**
     * 설문 저장 로직 처리
     */
    @ResponseBody
    @PostMapping(value = "insertTestResult")
    public MsgDTO insertTestResult(@RequestBody TestDTO testDTO, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".controller 자가진단 결과 저장 Start!");

        int result = 0; // 결과 코드
        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            // 로그인된 사용자 아이디 가져오기
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String userTestResult = CmmUtil.nvl(testDTO.getUserTestResult()); // 자가진단 결과

            log.info("ss_user_id : " + userId);
            log.info("userTestResult : " + userTestResult);

            if (userId.isEmpty() || userTestResult.isEmpty()) {
                throw new IllegalArgumentException("사용자 ID 또는 자가진단 결과가 유효하지 않습니다.");
            }

            // 데이터 저장하기 위해 DTO에 저장하기
            TestDTO pDTO = TestDTO.builder()
                    .userId(userId)
                    .userTestResult(userTestResult)
                    .build();

            // 챌린지 등록하기 위한 비즈니스 로직 호출
            testService.insertTestResult(pDTO);

            // 총 점수를 세션에 저장
            session.setAttribute("totalScore", Integer.parseInt(userTestResult));

            // 저장이 완료되면 사용자에게 보여줄 메시지
            msg = "등록되었습니다.";
            result = 1;

        } catch (Exception e) {
            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            // 결과 메시지 전달하기
            dto = MsgDTO.builder().result(result).msg(msg).build();

            log.info(this.getClass().getName() + ".controller 자가진단 결과 저장 End!");
        }
        return dto;
    }

    @GetMapping("testResult")
    public String testResult(ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".controller 자가진단 결과 시작 !");

        String ssUserId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("세션에 저장 되어있는 아이디('SS_USER_ID') : " + ssUserId);

//        if (ssUserId == null) {
//            model.addAttribute("msg", "로그인 후 이용 바랍니다.");
//            return "redirect:/user/login"; // 로그인 페이지로 리디렉션
//        }

        Integer totalScore = (Integer) session.getAttribute("totalScore");
        model.addAttribute("totalScore", totalScore);

        log.info(this.getClass().getName() + ".controller 자가진단 결과 끝 !");

        return "test/testResult";
    }
}
