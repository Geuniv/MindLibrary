package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.*;
import kopo.poly.service.*;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    private final IFileBoardService fileBoardService;

    private final IS3Service s3Service;

//    private final IProfileService profileService;

    private final ILikeService likeService;

    /** 커뮤니티 리스트 보여주기 */
    @GetMapping(value = "boardList")
    public String boardList(HttpSession session, ModelMap model, @RequestParam(defaultValue = "1") int page) throws Exception {

        log.info(this.getClass().getName() + ".controller 게시글 리스트 시작 !");

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

        log.info(this.getClass().getName() + ".controller 게시글 리스트 끝 !");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "board/boardList";
    }

    /** 커뮤니티 작성 페이지 이동*/
    @GetMapping(value = "boardReg")
    public String boardReg(){

        log.info(this.getClass().getName() + ".controller 게시글 작성 시작 !");

        log.info(this.getClass().getName() + ".controller 게시글 작성 끝 !");

        return "board/boardReg";
    }

    /** 커뮤니티 글 등록 */
    @ResponseBody
    @PostMapping(value = "boardInsert")
    public MsgDTO boardInsert(HttpServletRequest request, HttpSession session,
                              @RequestParam(value = "file", required = false) List<MultipartFile> files) throws Exception {

        log.info(this.getClass().getName() + ".controller 게시글 저장 시작 !");

        MsgDTO msgDTO = null;
        String msg = "";
        int res = 0;

        BoardDTO pDTO = null;

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID")); // 아이디
            String boardTitle = CmmUtil.nvl(request.getParameter("boardTitle")); // 제목
            String boardContent = CmmUtil.nvl(request.getParameter("boardContent")); // 내용

            log.info("ss_user_id : " + userId);
            log.info("boardTitle : " + boardTitle);
            log.info("boardContent : " + boardContent);

            String nextBoardSeq = boardService.getNextBoardSeq();

            pDTO = BoardDTO.builder()
                    .boardSeq(nextBoardSeq)
                    .userId(userId)
                    .boardTitle(boardTitle)
                    .boardContent(boardContent)
                    .build();

            res = boardService.insertBoardInfo(pDTO);

            if (files != null) {
                String saveFilePath = FileUtil.mkdirForData();      // 웹서버에 저장할 파일 경로 생성
                String boardSeq = pDTO.boardSeq();

                log.info("boardSeq : " + boardSeq);

                for (MultipartFile mf : files) {
                    log.info("mf : " + mf);

                    String orgFileName = mf.getOriginalFilename();      // 파일의 원본 명
                    String fileSize = String.valueOf(mf.getSize());     // 파일 크기
                    String ext = orgFileName.substring(orgFileName.lastIndexOf(".") + 1).toLowerCase();  // 확장자

                    // 이미지 파일만 실행되도록 함
                    if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("gif") || ext.equals("png")) {
                        log.info("boardSeq : " + boardSeq);
                        log.info("orgFileName : " + orgFileName);
                        log.info("fileSize : " + fileSize);
                        log.info("ext : " + ext);
                        log.info("saveFilePath : " + saveFilePath);

                        FileDTO fileDTO = new FileDTO();
                        fileDTO.setOrgFileName(orgFileName);
                        fileDTO.setFilePath(saveFilePath);
                        fileDTO.setFileSize(fileSize);
                        fileDTO.setBoardSeq(Integer.valueOf(boardSeq));
                        fileDTO.setUserId(userId); // userId 설정

                        FileDTO rDTO = s3Service.uploadFile(mf, ext);
                        fileDTO.setFileUrl(rDTO.getFileUrl());
                        fileDTO.setFileName(rDTO.getFileName());

                        log.info("sageFileUrl : " + rDTO.getFileUrl());

                        fileBoardService.insertBoardFile(fileDTO);
                    }
                }
            }

            msg = "등록되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            msgDTO = MsgDTO.builder().msg(msg).result(res).build();
            log.info(".controller 커뮤니티 글 등록 종료");
        }

        return msgDTO;
    }


    /** 게시판 상세보기 */
    @GetMapping(value = "boardInfo")
    public String boardInfo(HttpSession session, HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".controller 게시글 상세보기 시작 !");

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

        // 이미지 장수 가져오기
        FileDTO fDTO = fileBoardService.getCountPage(pDTO);

        // 이미지 가져오기
        List<FileDTO> fList = Optional.ofNullable(fileBoardService.getBoardFile(pDTO)).orElseGet(ArrayList::new);
        log.info("fList size: " + fList.size());
        for (FileDTO fileDTO : fList) {
            log.info("FileDTO: " + fileDTO.getFileUrl());
        }
        model.addAttribute("fList", fList);

        CommentDTO cDTO = new CommentDTO();
        cDTO.setBoardSeq(bSeq);

        // 댓글 리스트 조회하기
        List<CommentDTO> rList = Optional.ofNullable(commentService.getCommentList(cDTO)).orElseGet(ArrayList::new);

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        for (CommentDTO dto : rList) {
            log.info("commentSeq : " + dto.getCommentSeq());
            log.info("userNickname : " + dto.getUserNickname());
            log.info("getFileUrl : " + dto.getFileUrl());
        }

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);

        log.info("rDTO : " + rDTO.toString());

        // 좋아요

        LikeDTO lDTO = LikeDTO.builder().boardSeq(bSeq).build();

        int likeCnt = likeService.getLikeCount(lDTO);

        model.addAttribute("likeCnt", likeCnt);

        log.info(this.getClass().getName() + ".controller 게시글 상세보기 끝 !");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "board/boardInfo";
    }

    /** 게시판 수정을 위한 페이지 */
    @GetMapping(value = "boardEditInfo")
    public String boardEditInfo(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".controller 게시글 수정 상세보기 시작 !");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        String bSeq = CmmUtil.nvl(request.getParameter("bSeq")); // 공지글 번호(PK)

        log.info("세션 아이디 : " + userId);
        log.info("bSeq : " + bSeq);

        // 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
        BoardDTO pDTO = BoardDTO.builder().boardSeq(bSeq).userId(userId).build();

        BoardDTO rDTO = Optional.ofNullable(boardService.getBoardInfo(pDTO, false)).orElseGet(() -> BoardDTO.builder().build());

        // 이미지 장수 가져오기
        FileDTO fDTO = fileBoardService.getCountPage(pDTO);

        // 이미지 가져오기
        List<FileDTO> fList = Optional.ofNullable(fileBoardService.getBoardFile(pDTO)).orElseGet(ArrayList::new);
        log.info("fList size: " + fList.size());
        for (FileDTO fileDTO : fList) {
            log.info("FileDTO: " + fileDTO.getFileUrl());
        }
        model.addAttribute("fList", fList);

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".controller 게시글 수정 상세보기 끝 !");

        // 함수 처리가 끝나고 보여줄 html 파일명
        return "board/boardEditInfo";
    }

    /** 게시판 글 수정 */
    @ResponseBody
    @PostMapping(value = "boardUpdate")
    public MsgDTO boardUpdate(HttpSession session, HttpServletRequest request,
                              @RequestParam(value = "file", required = false) List<MultipartFile> files,
                              @RequestParam(value = "removedImages", required = false) List<String> removedImages) throws Exception {

        log.info(this.getClass().getName() + ".controller 게시글 수정하기 시작 !");

        String msg = ""; // 메시지 내용
        int res = 0;

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

            if (removedImages != null && !removedImages.isEmpty()) {
                for (String fileName : removedImages) {
                    // BoardDTO를 사용하여 파일 삭제
                    BoardDTO delDTO = BoardDTO.builder().boardSeq(boardSeq).fileName(fileName).build();
                    fileBoardService.deleteBoardFile(delDTO);
                }
            }

            if (files != null && !files.isEmpty()) {
                String saveFilePath = FileUtil.mkdirForData(); // 웹서버에 저장할 파일 경로 생성

                log.info("boardSeq : " + boardSeq);

                for (MultipartFile mf : files) {
                    log.info("mf : " + mf);

                    String orgFileName = mf.getOriginalFilename(); // 파일의 원본 명
                    String fileSize = String.valueOf(mf.getSize()); // 파일 크기
                    String ext = orgFileName.substring(orgFileName.lastIndexOf(".") + 1, // 확장자
                            orgFileName.length()).toLowerCase();

                    // 이미지 파일만 실행되도록 함
                    if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("gif") || ext.equals("png")) {
                        log.info("boardSeq : " + boardSeq);
                        log.info("orgFileName : " + orgFileName);
                        log.info("fileSize : " + fileSize);
                        log.info("ext : " + ext);
                        log.info("saveFilePath : " + saveFilePath);

                        FileDTO fileDTO = new FileDTO();
                        fileDTO.setOrgFileName(orgFileName);
                        fileDTO.setFilePath(saveFilePath);
                        fileDTO.setFileSize(fileSize);
                        fileDTO.setBoardSeq(Integer.valueOf(boardSeq)); // Integer 타입으로 변경
                        fileDTO.setUserId(userId);

                        FileDTO rDTO = s3Service.uploadFile(mf, ext);
                        fileDTO.setFileUrl(rDTO.getFileUrl());
                        fileDTO.setFileName(rDTO.getFileName());

                        log.info("saveFileUrl : " + rDTO.getFileUrl());

                        fileBoardService.insertBoardFile(fileDTO);
                    }
                }
            }

            msg = "수정되었습니다.";
            res = 1;

        } catch (Exception e) {
            msg = "실패하였습니다.";
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            MsgDTO dto = MsgDTO.builder().msg(msg).result(res).build();
            log.info(this.getClass().getName() + ".controller 게시글 수정하기 끝 !");
            return dto;
        }
    }

    /** 커뮤니티 글 삭제 */
    @ResponseBody
    @PostMapping(value = "boardDelete")
    public MsgDTO boardDelete(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".controller 게시글 삭제하기 시작 !");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String bSeq = CmmUtil.nvl(request.getParameter("bSeq")); // 글번호(PK)

            log.info("bSeq : " + bSeq);

            BoardDTO pDTO = BoardDTO.builder().boardSeq(bSeq).build();

            // 게시글 삭제하기 DB
            boardService.deleteBoardInfo(pDTO);

            // 파일 삭제하기 DB
//            fileBoardService.deleteBoardFile(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            //결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".controller 게시글 삭제하기 끝 !");
        }
        return dto;

    }
}
