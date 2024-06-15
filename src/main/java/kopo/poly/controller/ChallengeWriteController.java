package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.*;
import kopo.poly.service.IBookService;
import kopo.poly.service.IChallengeWriteService;
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
@RequestMapping(value = "/challenge/challengeWrite")
@RequiredArgsConstructor
@Controller
public class ChallengeWriteController {

    private final IChallengeWriteService challengeWriteService;

    private final IBookService bookService;

    /** 2024.05.27
     * 마음쓰기 리스트 보여주기
     */
    @GetMapping(value = "challengeWriteList")
    public String challengeWriteList(HttpSession session, HttpServletRequest request, ModelMap model,
                                     @RequestParam(defaultValue = "1") int page) throws Exception {

        log.info(this.getClass().getName() + ".challengeWriteList ( 컨트롤러 ) Start!");

        // TODO: 2023-10-23 session_user_id 변경하기
        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        String cSeq = CmmUtil.nvl(request.getParameter("cSeq")); // 공지글 번호(PK)
        String cwSeq = CmmUtil.nvl(request.getParameter("cwSeq")); // 공지글 번호(PK)

        ChallengeWriteDTO pDTO = new ChallengeWriteDTO();
        pDTO.setChallengeSeq(cSeq);
        pDTO.setChallengeWriteTitle(cwSeq);
        pDTO.setUserId(userId);

        // 커뮤니티 리스트 조회하기
        List<ChallengeWriteDTO> rList = Optional.ofNullable(challengeWriteService.getChallengeWriteList(pDTO)).orElseGet(ArrayList::new);

        log.info("rList : " + rList);

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

        // 조회된 리스트 결과값 넣어주기
//        model.addAttribute("rList", rList);
        model.addAttribute("pDTO", pDTO);

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);

        /**페이징 끝부분*/

        log.info(this.getClass().getName() + ".challengeWriteList ( 컨트롤러 ) End!");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "challenge/challengeWrite/challengeWriteList";
    }

    /** 마음쓰기 상세보기 */
    @GetMapping(value = "challengeWriteInfo")
    public String challengeWriteInfo(HttpSession session, HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".challengeWriteInfo ( 컨트롤러 ) Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        String cSeq = CmmUtil.nvl(request.getParameter("cSeq")); // 공지글 번호(PK)
        String cwSeq = CmmUtil.nvl(request.getParameter("cwSeq")); // 공지글 번호(PK)

        log.info("세션 아이디 : " + userId);
        log.info("cSeq : " + cSeq);
        log.info("cwSeq : " + cwSeq);

        // 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
        ChallengeWriteDTO pDTO = new ChallengeWriteDTO();
        pDTO.setChallengeSeq(cSeq);
        pDTO.setChallengeWriteSeq(cwSeq);

        // 공지사항 상세정보 가져오기
        ChallengeWriteDTO rDTO = Optional.ofNullable(challengeWriteService.getChallengeWriteInfo(pDTO, true)).orElseGet(() -> ChallengeWriteDTO.builder().build());

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);

        log.info("rDTO : " + rDTO.toString());

        log.info(this.getClass().getName() + ".challengeWriteInfo ( 컨트롤러 ) End!");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "challenge/challengeWrite/challengeWriteInfo";
    }

    /** 2024.05.26
     * 마음쓰기 생성 페이지 이동
     */
    @GetMapping(value = "challengeWriteReg")
    public String challengeWriteReg(HttpServletRequest request, ModelMap modelMap, HttpSession
                                     session){

        log.info(this.getClass().getName() + ".challengeWriteReg ( 컨트롤러 ) Start !");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        String cSeq = CmmUtil.nvl(request.getParameter("cSeq")); // 공지글 번호(PK)
        String cwSeq = CmmUtil.nvl(request.getParameter("cwSeq")); // 공지글 번호(PK)

        ChallengeWriteDTO pDTO = new ChallengeWriteDTO();
        pDTO.setChallengeSeq(cSeq);
        pDTO.setChallengeWriteTitle(cwSeq);
        pDTO.setUserId(userId);

        // 조회된 리스트 결과값 넣어주기
//        modelMap.addAttribute("rList", rList);
        modelMap.addAttribute("pDTO", pDTO);

        log.info(this.getClass().getName() + ".challengeWriteReg ( 컨트롤러 ) End !");

        return "challenge/challengeWrite/challengeWriteReg";
    }

    /**  2024.05.27
     * 마음쓰기 등록
     */
    @ResponseBody
    @PostMapping(value = "challengeWriteInsert")
    public MsgDTO challengeWriteInsert(HttpServletRequest request, HttpSession session, ModelMap model) {

        log.info(this.getClass().getName() + ".challengeWriteInsert ( 컨트롤러 ) Start!");

        String msg = "";
        MsgDTO dto = null;

        String userId = (String) session.getAttribute("SS_USER_ID");
        log.info("세션에 저장 되어있는 유저 아이디('SS_USER_ID') : " + session.getAttribute("SS_USER_ID"));

        try {
            String challengeSeq = CmmUtil.nvl(request.getParameter("challengeSeq"));
            String challengeWriteTitle = CmmUtil.nvl(request.getParameter("challengeWriteTitle"));
            String challengeWriteContent = CmmUtil.nvl(request.getParameter("challengeWriteContent"));
            String challengeWriteBooktitle = CmmUtil.nvl(request.getParameter("challengeWriteBooktitle"));
            String challengeWriteBookimage = CmmUtil.nvl(request.getParameter("challengeWriteBookimage"));
            String bookLink = CmmUtil.nvl(request.getParameter("challengeWriteBookLink"));
            String bookAuthor = CmmUtil.nvl(request.getParameter("challengeWriteBookAuthor"));
            String bookDiscount = CmmUtil.nvl(request.getParameter("challengeWriteBookDiscount"));
            String bookPublisher = CmmUtil.nvl(request.getParameter("challengeWriteBookPublisher"));
            String bookPubdate = CmmUtil.nvl(request.getParameter("challengeWriteBookPubdate"));
            String bookIsbn = CmmUtil.nvl(request.getParameter("challengeWriteBookIsbn"));
            String bookDescription = CmmUtil.nvl(request.getParameter("challengeWriteBookDescription"));

            log.info("userId ( 세션 ) : " + userId);
            log.info("challengeSeq ( 마음쓰기 챌린지 순번 ) : " + challengeSeq);
            log.info("challengeWriteTitle ( 마음쓰기 제목 ) : " + challengeWriteTitle);
            log.info("challengeWriteContent ( 마음쓰기 내용 ) : " + challengeWriteContent);
            log.info("challengeWriteBooktitle ( API 호출 책 제목 ) : " + challengeWriteBooktitle);
            log.info("challengeWriteBookimage ( API 호출 책 이미지 ) : " + challengeWriteBookimage);
            log.info("bookLink ( API 호출 책 링크 ) : " + bookLink);
            log.info("bookAuthor ( API 호출 작가 ) : " + bookAuthor);
            log.info("bookDiscount ( API 호출 할인가 ) : " + bookDiscount);
            log.info("bookPublisher ( API 호출 출판사 ) : " + bookPublisher);
            log.info("bookPubdate ( API 호출 발행일 ) : " + bookPubdate);
            log.info("bookIsbn ( API 호출 Isbn ) : " + bookIsbn);
            log.info("bookDescription ( API 호출 설명 ) : " + bookDescription);

            if (challengeSeq.isEmpty()) {
                throw new IllegalArgumentException("챌린지 순번이 유효하지 않습니다.");
            }

            // 데이터 저장하기 위해 DTO에 저장하기
            ChallengeWriteDTO pDTO = new ChallengeWriteDTO();
            pDTO.setChallengeSeq(challengeSeq);
            pDTO.setUserId(userId);
            pDTO.setChallengeWriteTitle(challengeWriteTitle);
            pDTO.setChallengeWriteContent(challengeWriteContent);
            pDTO.setChallengeWriteBooktitle(challengeWriteBooktitle);
            pDTO.setChallengeWriteBookimage(challengeWriteBookimage);

            BookSearchDTO bDTO = new BookSearchDTO();
            bDTO.setTitle(challengeWriteBooktitle);
            bDTO.setImage(challengeWriteBookimage);
            bDTO.setLink(bookLink);
            bDTO.setAuthor(bookAuthor);
            bDTO.setDiscount(bookDiscount);
            bDTO.setPublisher(bookPublisher);
            bDTO.setIsbn(bookIsbn);
            bDTO.setPubdate(bookPubdate);
            bDTO.setDescription(bookDescription);

            // 게시글 등록하기 위한 비즈니스 로직 호출
            challengeWriteService.insertChallengeWriteInfo(pDTO);

            model.addAttribute("pDTO : ", pDTO);

            // 저장이 완료되면 사용자에게 보여줄 메시지
            msg = "등록되었습니다.";
        } catch (IllegalArgumentException e) {
            msg = "입력 데이터가 유효하지 않습니다: " + e.getMessage();
            log.info(e.toString());
        } catch (Exception e) {
            msg = "등록에 실패했습니다. 다시 시도해주세요.";
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            dto = MsgDTO.builder().msg(msg).build();
            log.info(this.getClass().getName() + ".challengeWriteInsert ( 컨트롤러 ) End!");
        }

        return dto;
    }

    /** 게시판 수정을 위한 페이지*/
    @GetMapping(value = "challengeWriteEditInfo")
    public String challengeWriteEditInfo(HttpServletRequest request, ModelMap modelMap, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".challengeWriteEditInfo ( 컨트롤러 ) Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        String cSeq = CmmUtil.nvl(request.getParameter("cSeq")); // 공지글 번호(PK)
        String cwSeq = CmmUtil.nvl(request.getParameter("cwSeq"));

        log.info("세션 아이디 : " + userId);
        log.info("cSeq : " + cSeq);
        log.info("cwSeq : " + cwSeq);

        // 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
        ChallengeWriteDTO pDTO = ChallengeWriteDTO.builder().challengeSeq(cSeq).challengeWriteSeq(cwSeq).userId(userId).build();

        ChallengeWriteDTO rDTO = Optional.ofNullable(challengeWriteService.getChallengeWriteInfo(pDTO, false)).orElseGet(() -> ChallengeWriteDTO.builder().build());

        // 조회된 리스트 결과값 넣어주기
        modelMap.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".challengeWriteEditInfo ( 컨트롤러 ) End!");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "challenge/challengeWrite/challengeWriteEditInfo";
    }

    /** 게시판 글 수정 */
    @ResponseBody
    @PostMapping("challengeWriteUpdate")
    public MsgDTO challengeWriteUpdate(HttpSession session, @RequestBody ChallengeWriteDTO pDTO) {

        log.info(this.getClass().getName() + ".challengeWriteUpdate ( 컨트롤러 ) Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID")); // 아이디
            pDTO.setUserId(userId);

            log.info(" 여기 있어요 userId : " + userId);
            log.info("challengeSeq : " + pDTO.getChallengeSeq());
            log.info("challengeWriteTitle : " + pDTO.getChallengeWriteTitle());
            log.info("challengeWriteContent : " + pDTO.getChallengeWriteContent());

            log.info(pDTO.toString());

            // 게시글 수정하기 DB
            challengeWriteService.updateChallengeWriteInfo(pDTO);

            msg = "수정되었습니다.";
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".challengeWriteUpdate ( 컨트롤러 ) End!");
        }
        return dto;
    }

    /** 2024.05.27
     * 마음쓰기 삭제
     */
    @ResponseBody
    @PostMapping(value = "challengeWriteDelete")
    public MsgDTO challengeWriteDelete(@RequestBody ChallengeWriteDTO pDTO, HttpSession session) {

        log.info(this.getClass().getName() + ".challengeWriteDelete ( 컨트롤러 ) Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            log.info("SS_USER_ID : " + userId);
            log.info("challengeWriteSeq : " + pDTO.getChallengeWriteSeq());

            pDTO.setUserId(userId);

            // 게시글 삭제하기 DB
            challengeWriteService.deleteChallengeWriteInfo(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            //결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".challengeWriteDelete ( 컨트롤러 ) End!");
        }
        return dto;
    }
}
