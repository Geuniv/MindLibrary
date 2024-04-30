package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.*;
import kopo.poly.service.IBoardService;
import kopo.poly.service.ICommentService;
import kopo.poly.service.IFileService;
import kopo.poly.service.IProfileService;
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
@RequestMapping(value = "/board")
//특정 메소드와 매핑하기 위해 사용, 구형이다
@RequiredArgsConstructor
@Controller
public class BoardController {

    private final IBoardService boardService;

    private final ICommentService commentService;

    private final IFileService fileService;

    private final IProfileService profileService;

    /** 커뮤니티 리스트 보여주기 */
    @GetMapping(value = "boardList")
    public String boardList(HttpSession session, ModelMap model, @RequestParam(defaultValue = "1") int page) throws Exception {

        log.info(this.getClass().getName() + ".boardList Start!");

        String ssUserId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("세션에 저장 되어있는 아이디('SS_USER_ID') : " + ssUserId);

        List<Map<String, Object>> pList = boardService.getBoardList();
        if (pList == null) pList = new ArrayList<>();

        log.info(pList.toString());

        List<BoardDTO> rList = new ArrayList<>();

        for (Map<String, Object> rMap : pList) {
            BoardDTO rDTO = BoardDTO.builder().boardSeq(String.valueOf(rMap.get("boardSeq"))
            ).notification(String.valueOf(rMap.get("notification"))
            ).boardTitle(String.valueOf(rMap.get("boardTitle"))
            ).userId(String.valueOf(rMap.get("userId"))
            ).boardContent(String.valueOf(rMap.get("boardContent"))
            ).userNickname(String.valueOf(rMap.get("userNickname"))
            ).boardViews(String.valueOf(rMap.get("boardViews"))
            ).boardRegDt(String.valueOf(rMap.get("boardRegDt"))
            ).boardRegId(String.valueOf(rMap.get("boardRegId"))
            ).build();

            rList.add(rDTO);
        }

        /**페이징 시작 부분*/

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 10;

        // 페이지네이션을 위해 전체 아이템 개수 구하기
        int totalItems = rList.size();

        // 전체 페이지 개수 계산
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // 현재 페이지에 해당하는 아이템들만 선택하여 rList에 할당
        int fromIndex = (page - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
        rList = rList.subList(fromIndex, toIndex);


        model.addAttribute("rList", rList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);

        /**페이징 끝부분*/

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        log.info(rList.toString());

        log.info(this.getClass().getName() + ".boardList End!");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "/board/boardList";
    }

    /** 커뮤니티 작성 페이지 이동*/
    @GetMapping(value = "boardReg")
    public String boardReg(){

        log.info(this.getClass().getName() + ".boardReg Start!");

        log.info(this.getClass().getName() + ".boardReg End!");

        return "/board/boardReg";
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

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        String bSeq = CmmUtil.nvl(request.getParameter("bSeq")); // 공지글 번호(PK)
        String userNickname = CmmUtil.nvl(request.getParameter("userNickname")); // 공지글 번호(PK)

        log.info("세션 아이디 : " + userId);
        log.info("bSeq : " + bSeq);
        log.info("userNickname : " + userNickname);

        // 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
        BoardDTO pDTO = BoardDTO.builder().boardSeq(bSeq).userId(userId).userNickname(userNickname).build();

        // 공지사항 상세정보 가져오기
        BoardDTO rDTO = Optional.ofNullable(boardService.getBoardInfo(pDTO, true)).orElseGet(() -> BoardDTO.builder().build());

        CommentDTO cDTO = new CommentDTO();
        cDTO.setBoardSeq(bSeq);

        // 댓글 리스트 조회하기
        List<CommentDTO> rList = Optional.ofNullable(commentService.getCommentList(cDTO)).orElseGet(ArrayList::new);

        // 조회된 리스트 결과값 넣어주기
        modelMap.addAttribute("rList", rList);

        for (CommentDTO dto : rList) {
            log.info("commentSeq : " + dto.getCommentSeq());
            log.info("userNickname : " + dto.getUserNickname());
        }

        // 조회된 리스트 결과값 넣어주기
        modelMap.addAttribute("rDTO", rDTO);

        log.info("rDTO : " + rDTO.toString());

//        UserInfoDTO uDTO = new UserInfoDTO();
//        uDTO.setUserId(userId);
//
//        UserInfoDTO fDTO = Optional.ofNullable(profileService.getProfile(uDTO)).orElseGet(UserInfoDTO::new);
//
//        log.info("pDTO : " + pDTO);
//
//        // 이미지 가져오기
//        List<FileDTO> fList = Optional.ofNullable(fileService.getFile(uDTO)).orElseGet(ArrayList::new);
//
//        log.info("fList : " + fList);
//
//        modelMap.addAttribute("fList", fList);
//
//        log.info("fList : " + fList);
//
//        // 이미지 가져오기 종료

        log.info(this.getClass().getName() + ".boardInfo End!");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "/board/boardInfo";
    }

    /** 게시판 수정을 위한 페이지*/
    @GetMapping(value = "boardEditInfo")
    public String boardEditInfo(HttpServletRequest request, ModelMap modelMap, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".boardEditInfo Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        String bSeq = CmmUtil.nvl(request.getParameter("bSeq")); // 공지글 번호(PK)

        log.info("세션 아이디 : " + userId);
        log.info("bSeq : " + bSeq);

        // 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
        BoardDTO pDTO = BoardDTO.builder().boardSeq(bSeq).userId(userId).build();

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
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID")); // 아이디
            String boardSeq = CmmUtil.nvl(request.getParameter("boardSeq")); // 글번호(PK)
            String boardTitle = CmmUtil.nvl(request.getParameter("boardTitle")); // 제목
            String notification = CmmUtil.nvl(request.getParameter("notification")); // 공지글 여부
            String boardContent = CmmUtil.nvl(request.getParameter("boardContent")); // 내용

            log.info(" 여기 있어요 userId : " + userId);
            log.info("boardSeq : " + boardSeq);
            log.info("boardTitle : " + boardTitle);
            log.info("notification : " + notification);
            log.info("boardContent : " + boardContent);

            BoardDTO pDTO = BoardDTO.builder()
                    .userId(userId)
                    .boardSeq(boardSeq)
                    .boardTitle(boardTitle)
                    .boardContent(boardContent).build();

            log.info(pDTO.toString());

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
