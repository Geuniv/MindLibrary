package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.BoardDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.service.IBoardService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/board")
@RequiredArgsConstructor
@Controller
public class BoardController {

    private final IBoardService boardService;
//    private final ICommentService commentService;

    /** 커뮤니티 리스트 보여주기 */
    @GetMapping(value = "boardList")
    public String boardList(HttpSession session, ModelMap model, @RequestParam(defaultValue = "1") int page) throws Exception {

        log.info(this.getClass().getName() + ".boardList Start!");

        String userId = (String) session.getAttribute("SS_USER_ID"); // 아이디 세션값

        log.info("세션에 저장 되어있는 아이디('SS_USER_ID') : " + userId);

        BoardDTO pDTO = BoardDTO.builder().userId(userId).build();

        BoardDTO rDTO = Optional.ofNullable(boardService.getBoardInfo(pDTO, true))
                .orElseGet(() -> BoardDTO.builder().build());

        model.addAttribute("rDTO", rDTO);

        // 커뮤니티 리스트 조회하기
        List<BoardDTO> rList = Optional.ofNullable(boardService.getBoardList()).orElseGet(ArrayList::new);

        /**페이징 시작 부분*/

//        // 페이지당 보여줄 아이템 개수 정의
//        int itemsPerPage = 10;
//
//        // 페이지네이션을 위해 전체 아이템 개수 구하기
//        int totalItems = rList.size();
//
//        // 전체 페이지 개수 계산
//        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
//
//        // 현재 페이지에 해당하는 아이템들만 선택하여 rList에 할당
//        int fromIndex = (page - 1) * itemsPerPage;
//        int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
//        rList = rList.subList(fromIndex, toIndex);
//
//
//        model.addAttribute("rList", rList);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", totalPages);
//
//        log.info(this.getClass().getName() + ".페이지 번호 : " + page);

        /**페이징 끝부분*/

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".boardList End!");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "board/boardList";
    }

    /** 커뮤니티 작성 페이지 이동*/
    @GetMapping(value = "boardReg")
    public String boardReg(){

        log.info(this.getClass().getName() + ".boardReg Start!");

        log.info(this.getClass().getName() + ".boardReg End!");

        return "board/boardReg";
    }

    /** 커뮤니티 글 등록 */
    @ResponseBody
    @PostMapping(value = "boardInsert")
    public MsgDTO boardInsert(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".boardInsert Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            // 로그인된 사용자 아이디 가져오기
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String boardTitle = CmmUtil.nvl(request.getParameter("boardTitle")); // 제목
            String boardContent = CmmUtil.nvl(request.getParameter("boardContent")); // 내용


            log.info("ss_user_id : " + userId);
            log.info("boardTitle : " + boardTitle);
            log.info("boardContent : " + boardContent);

            // 데이터 저장하기 위해 DTO에 저장하기
            BoardDTO pDTO = BoardDTO.builder().userId(userId).boardTitle(boardTitle).boardContent(boardContent).build();

            // 게시글 등록하기 위한 비즈니스 로직 호출
            boardService.insertBoardInfo(pDTO);

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

            log.info(this.getClass().getName() + ".boardInsert End!");
        }
        return dto;
    }

    /** 게시판 상세보기 */
    @GetMapping(value = "boardInfo")
    public String boardInfo(HttpSession session, HttpServletRequest request, ModelMap modelMap) throws Exception {

        log.info(this.getClass().getName() + ".boardInfo Start!");

        String bSeq = CmmUtil.nvl(request.getParameter("bSeq")); // 공지글 번호(PK)

        log.info("bSeq : " + bSeq);

        // 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
        BoardDTO pDTO = BoardDTO.builder().boardSeq(bSeq).build();

        // 공지사항 상세정보 가져오기
        BoardDTO rDTO = Optional.ofNullable(boardService.getBoardInfo(pDTO, true)).orElseGet(() -> BoardDTO.builder().build());

//        CommentDTO cDTO = new CommentDTO();
//        cDTO.setCommunitySeq(nSeq);
//
//        // 댓글 리스트 조회하기
//        List<CommentDTO> rList = Optional.ofNullable(commentService.getCommentList(cDTO)).orElseGet(ArrayList::new);
//
//        // 조회된 리스트 결과값 넣어주기
//        modelMap.addAttribute("rList", rList);
//
//        for (CommentDTO dto : rList) {
//            log.info("commentSeq" + dto.getCommentSeq());
//        }

        // 조회된 리스트 결과값 넣어주기
        modelMap.addAttribute("rDTO", rDTO);

        log.info("rDTO : " + rDTO.toString());

        log.info(this.getClass().getName() + ".boardInfo End!");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "/board/boardInfo";
    }

    /** 게시판 수정을 위한 페이지*/
    @GetMapping(value = "boardEditInfo")
    public String boardEditInfo(HttpServletRequest request, ModelMap modelMap) throws Exception {

        log.info(this.getClass().getName() + ".boardEditInfo Start!");

        String bSeq = CmmUtil.nvl(request.getParameter("bSeq")); // 공지글 번호

        log.info("bSeq : " + bSeq);

        // 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
        BoardDTO pDTO = BoardDTO.builder().boardSeq(bSeq).build();

        BoardDTO rDTO = Optional.ofNullable(boardService.getBoardInfo(pDTO, false)).orElseGet(() -> BoardDTO.builder().build());

        // 조회된 리스트 결과값 넣어주기
        modelMap.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".boardEditInfo End!");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "/board/boardEditInfo";
    }

    /** 게시판 글 수정 */
    @ResponseBody
    @PostMapping(value = "boardUpdate")
    public MsgDTO boardUpdate(HttpSession session, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".boardUpdate Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID")); // 아이디
            String bSeq = CmmUtil.nvl(request.getParameter("bSeq")); // 글번호(PK)
            String title = CmmUtil.nvl(request.getParameter("title")); // 제목
            String communityYn = CmmUtil.nvl(request.getParameter("communityYn")); // 공지글 여부
            String contents = CmmUtil.nvl(request.getParameter("contents")); // 내용

            log.info("userId : " + userId);
            log.info("bSeq : " + bSeq);
            log.info("title : " + title);
            log.info("communityYn : " + communityYn);
            log.info("contents : " + contents);

            BoardDTO pDTO = BoardDTO.builder().userId(userId).boardSeq(bSeq).boardTitle(title).boardContent(contents).build();

            // 게시글 수정하기 DB
            boardService.updateBoardInfo(pDTO);

            msg = "수정되었습니다.";
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".boardUpdate End!");
        }
        return dto;
    }

    /** 커뮤니티 글 삭제 */
    @ResponseBody
    @PostMapping(value = "boardDelete")
    public MsgDTO boardDelete(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".boardDelete Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String bSeq = CmmUtil.nvl(request.getParameter("bSeq")); // 글번호(PK)

            log.info("bSeq : " + bSeq);

            BoardDTO pDTO = BoardDTO.builder().boardSeq(bSeq).build();

            // 게시글 삭제하기 DB
            boardService.deleteBoardInfo(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            //결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".boardDelete End!");
        }
        return dto;

    }
}
