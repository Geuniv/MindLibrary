package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.*;
import kopo.poly.service.IBookService;
import kopo.poly.service.IChallengeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/challenge")
//특정 메소드와 매핑하기 위해 사용, 구형이다
@RequiredArgsConstructor
@Controller
public class ChallengeController {

    private final IChallengeService challengeServicee;

//    private final IBookService bookService;

    /**
     * 2024.05.26
     * 챌린지 리스트 보여주기
     */
    @GetMapping(value = "challengeList")
    public String getChallengeListByUserId(HttpSession session, HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".getChallengeListByUserId ( 컨트롤러 ) Start!");

        // 로그인된 사용자 아이디 가져오기
        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
        String cSeq = CmmUtil.nvl(request.getParameter("cSeq"));

        ChallengeDTO pDTO = ChallengeDTO.builder().challengeSeq(cSeq).build();

        try {
            // 사용자 ID로 챌린지 리스트 가져오기
            List<Map<String, Object>> rList = challengeServicee.getChallengeList(userId);

            // 모델에 챌린지 리스트 저장하기
            model.addAttribute("rList", rList);
            model.addAttribute("pDTO", pDTO);

        } catch (Exception e) {
            log.info("챌린지 리스트 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }

        log.info(this.getClass().getName() + ".getChallengeListByUserId ( 컨트롤러 ) End!");

        return "challenge/challengeList"; // challengeList.html 뷰 반환
    }

    // 예외처리 로직 나중에 쓸 예정
//    /** 2024.05.26
//     * 챌린지 리스트 보여주기
//     */
//    @GetMapping(value = "challengeList")
//    public String getChallengeListByUserId(HttpSession session, HttpServletRequest request, ModelMap model) throws Exception {
//
//        log.info(this.getClass().getName() + ".getChallengeListByUserId ( 컨트롤러 ) Start!");
//
//        // 로그인된 사용자 아이디 가져오기
//        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
//        String cSeq = CmmUtil.nvl(request.getParameter("cSeq"));
//
//        // 사용자 ID가 없으면 로그인 페이지로 리디렉션
//        if (userId.isEmpty()) {
//            model.addAttribute("msg", "로그인 후 이용 바랍니다.");
//            return "redirect:/user/login";
//        }
//
//        ChallengeDTO pDTO = ChallengeDTO.builder().challengeSeq(cSeq).build();
//
//        try {
//            // 사용자 ID로 챌린지 리스트 가져오기
//            List<Map<String, Object>> rList = challengeServicee.getChallengeList(userId);
//
//            if (rList != null) {
//                log.info("챌린지 리스트 조회 성공: 리스트 크기 = " + rList.size());
//                // 모델에 챌린지 리스트 저장하기
//                model.addAttribute("rList", rList);
//            } else {
//                log.info("챌린지 리스트 조회 결과 없음.");
//                model.addAttribute("rList", new ArrayList<>());
//            }
//
//            model.addAttribute("pDTO", pDTO);
//
//        } catch (Exception e) {
//            log.error("챌린지 리스트 조회 실패: " + e.getMessage(), e);
//        }
//
//        log.info(this.getClass().getName() + ".getChallengeListByUserId ( 컨트롤러 ) End!");
//
//        return "challenge/challengeList"; // challengeList.html 뷰 반환
//    }

    /**
     * 2024.05.26
     * 챌린지 생성 페이지 이동
     */
    @GetMapping(value = "challengeReg")
    public String challengeReg(ModelMap model, HttpSession session, HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".challengeReg ( 컨트롤러 ) Start !");

        // 로그인된 사용자 아이디 가져오기
        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("세션에 저장 되어있는 아이디('SS_USER_ID') : " + userId);

        log.info(this.getClass().getName() + ".challengeReg ( 컨트롤러 ) End !");

        return "challenge/challengeReg";
    }

    /**
     * 2024.05.26
     * 챌린지 생성 정보 등록
     */
    @ResponseBody
    @PostMapping(value = "challengeInsert")
    public MsgDTO challengeInsert(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".challengeInsert ( 컨트롤러 ) Start!");

        int result = 0; // 결과 코드
        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            // 로그인된 사용자 아이디 가져오기
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String userNickname = CmmUtil.nvl((String) session.getAttribute("SS_USER_NICKNAME"));
            String challengeDivision = CmmUtil.nvl(request.getParameter("challengeDivision")); // 챌린지 구분
            String challengeComment = CmmUtil.nvl(request.getParameter("challengeComment")); // 챌린지 설명
            String challengeStartDate = CmmUtil.nvl(request.getParameter("challengeStartDate")); // 챌린지 시작일
            String challengeEndDate = CmmUtil.nvl(request.getParameter("challengeEndDate")); // 챌린지 종료일

            log.info("ss_user_id : " + userId);
            log.info("userNickname : " + userNickname);
            log.info("challengeDivision : " + challengeDivision);
            log.info("challengeComment : " + challengeComment);
            log.info("challengeStartDate : " + challengeStartDate);
            log.info("challengeEndDate : " + challengeEndDate);

            // 데이터 저장하기 위해 DTO에 저장하기
            ChallengeDTO pDTO = ChallengeDTO.builder()
                    .userId(userId)
                    .userNickname(userNickname)
                    .challengeDivision(challengeDivision)
                    .challengeComment(challengeComment)
                    .challengeStartDate(challengeStartDate)
                    .challengeEndDate(challengeEndDate)
                    .build();

            // 챌린지 등록하기 위한 비즈니스 로직 호출
            challengeServicee.insertChallengeInfo(pDTO);

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

            log.info(this.getClass().getName() + ".challengeInsert ( 컨트롤러 ) End!");
        }
        return dto;
    }

    /**
     * 커뮤니티 글 삭제
     */
    @ResponseBody
    @PostMapping(value = "challengeDelete")
    public MsgDTO challengeDelete(@RequestBody Map<String, String> payload) throws Exception {

        log.info(this.getClass().getName() + ".challengeDelete ( 컨트롤러 ) Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String cSeq = CmmUtil.nvl(payload.get("cSeq")); // 글번호(PK)

            log.info("cSeq : " + cSeq);

            ChallengeDTO pDTO = ChallengeDTO.builder().challengeSeq(cSeq).build();

            // 게시글 삭제하기 DB
            challengeServicee.deleteChallengeInfo(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            //결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".challengeDelete ( 컨트롤러 ) End!");
        }
        return dto;
    }
}

