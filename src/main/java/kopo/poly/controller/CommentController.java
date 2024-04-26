package kopo.poly.controller;

import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.service.ICommentService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final ICommentService commentService;

    /** 커뮤니티 리스트 보여주기 */
//    @GetMapping(value = "commentList")
//    public String commentList(HttpSession session, ModelMap modelMap) throws Exception {
//
//        log.info(this.getClass().getName() + ".commentList Start!");
//
//        // TODO: 2023-10-23 session_user_id 변경하기
//        session.setAttribute("SESSION_USER_ID", "USER01");
//
//        // 커뮤니티 리스트 조회하기
//        List<CommentDTO> rList = Optional.ofNullable(commentService.getCommentList()).orElseGet(ArrayList::new);
//
//        // 조회된 리스트 결과값 넣어주기
//        modelMap.addAttribute("rList", rList);
//
//        log.info(this.getClass().getName() + ".commentList End!");
//
//        // 함수 처리가 끝나고 보여줄 html 파일명
//        return "community/communityInfo";
//    }


    /** 커뮤니티 글 등록 */
    @ResponseBody
    @PostMapping(value = "commentInsert")
    public MsgDTO commentInsert(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".commentInsert Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조x

        String userId = (String) session.getAttribute("SS_USER_ID"); // 유저 아이디
//        session.setAttribute("SS_USER_ID", reviewerId);
        log.info("세션에 저장 되어있는 유저 아이디('SS_USER_ID') : " + session.getAttribute("SS_USER_ID"));

        try {

            String boardSeq = CmmUtil.nvl(request.getParameter("boardSeq")); // 게시글 순번
            String commentContents = CmmUtil.nvl(request.getParameter("commentContents")); // 내용

            log.info("userId : " + userId);
            log.info("boardSeq : " + boardSeq);
            log.info("commentContents : " + commentContents);

            // 데이터 저장하기 위해 DTO에 저장하기
            CommentDTO pDTO = new CommentDTO();
            pDTO.setBoardSeq(boardSeq);
            pDTO.setUserId(userId);
            pDTO.setCommentContents(commentContents);

            // 게시글 등록하기 위한 비즈니스 로직 호출
            commentService.insertCommentInfo(pDTO);

            // 저장이 완료되면 사용자에게 보여줄 메시지
            msg = "등록되었습니다.";

        } catch (Exception e) {

            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".commentInsert End!");
        }
        return dto;
    }

    /**댓글 삭제*/
    @ResponseBody
    @PostMapping(value = "commentDelete")
    public MsgDTO commentDelete(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".commentDelete Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {

            String commentSeq = CmmUtil.nvl(request.getParameter("commentSeq")); // 글번호(PK)
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            log.info("commentSeq : " + commentSeq);
            log.info("SS_USER_ID : " + userId);

            CommentDTO pDTO = new CommentDTO();
            pDTO.setCommentSeq(commentSeq);
            pDTO.setUserId(userId);

            // 게시글 삭제하기 DB
            commentService.deleteCommentInfo(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            //결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".commentDelete End!");
        }
        return dto;

    }
}