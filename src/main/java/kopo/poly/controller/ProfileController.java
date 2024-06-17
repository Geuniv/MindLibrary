package kopo.poly.controller;

import kopo.poly.dto.*;
import kopo.poly.service.*;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import kopo.poly.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/profile")
@RequiredArgsConstructor
@Controller
public class ProfileController {

    private final IProfileService profileService; // 프로필 서비스

    private final IUserInfoService userInfoService; // 유저정보 서비스

    private final IFileService fileService; // 파일 서비스

    private final IS3Service s3Service; // 오브젝트 스토리지

    private final IBoardService boardService; // 게시판 서비스

    private final ICheckService checkService; // 챗 GPT ( 마음체크 ) 서비스
    
    private final ITestService testService; // 자가진단 서비스

    /* 프로필 조회 */
    @GetMapping(value = "")
    public String profile(ModelMap model, HttpSession session,
                          @RequestParam(defaultValue = "1") int page) throws Exception {

        log.info(this.getClass().getName() + "마이페이지 조회 컨트롤러 시작!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("SS_USER_ID : " + userId);

        if (userId == null || userId.isEmpty()) {
            log.warn("User ID is null or empty, redirecting to login page");
            model.addAttribute("msg", "로그인 후 이용 가능합니다.");
            model.addAttribute("url", "/index");
            log.info("Redirecting to /redirect with message: 로그인 후 이용 가능합니다.");
            return "redirect";
        }

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        UserInfoDTO rDTO = Optional.ofNullable(profileService.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("pDTO : " + pDTO);

        log.info("복호화 전 Email : " + rDTO.getUserEmail());
        String email = rDTO.getUserEmail();
        if (email != null && !email.isEmpty()) {
            try {
                rDTO.setUserEmail(EncryptUtil.decAES128CBC(email));
                log.info("복호화 후 Email : " + rDTO.getUserEmail());
            } catch (Exception e) {
                log.error("Email decryption failed", e);
                rDTO.setUserEmail("Decoding error");
            }
        } else {
            log.warn("Email is null or empty");
        }

        model.addAttribute("rDTO", rDTO);

        log.info("회원정보 조회 rDTO.toString() : " + rDTO.toString());

        List<BoardDTO> userPosts = boardService.getPostsByUserId(userId);
        log.info("userPosts : " + userPosts);

        model.addAttribute("userPosts", userPosts);

        CheckDTO cDTO = new CheckDTO();
        cDTO.setUserId(userId);
        List<CheckDTO> userChecks = checkService.getCheckByUserId(cDTO);
        log.info("userChecks : " + userChecks);

        model.addAttribute("userChecks", userChecks);

        TestDTO tDTO = new TestDTO();
        tDTO.setUserId(userId);
        List<TestDTO> userTests = testService.getTestByUserId(tDTO);
        log.info("userTests : " + userTests);

        model.addAttribute("userTests", userTests);

        log.info(this.getClass().getName() + "마이페이지 조회 컨트롤러 종료!");

        return "user/profile";
    }



    /* 프로필 수정 화면 */
    @GetMapping(value = "profileModify")
    public String profileModify(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + "마이페이지 수정페이지 보여주기 시작!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("세션에서 받아온 userId : " + userId);

        // userId가 null이거나 빈 문자열일 경우 redirect.html을 이용하여 /index 페이지로 리다이렉트 or 메시지 알림
        if (userId == null || userId.isEmpty()) {
            log.warn("User ID is null or empty, redirecting to login page");
            model.addAttribute("msg", "로그인 후 이용 가능합니다.");
            model.addAttribute("url", "/index"); // 로그인 페이지 URL로 수정
            log.info("Redirecting to /redirect with message: 로그인 후 이용 가능합니다.");
            return "redirect";
        }

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        log.info("pDTO : " + pDTO);

        UserInfoDTO rDTO = Optional.ofNullable(profileService.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("DB에서 가져와 복호화 하기 전 이메일 : " + rDTO.getUserEmail());

        rDTO.setUserEmail(EncryptUtil.decAES128CBC(rDTO.getUserEmail()));

        log.info("DB에서 가져와 복호화 하고나서 이메일 : " + rDTO.getUserEmail());

        model.addAttribute("rDTO", rDTO);

        log.info("회원정보 조회 rDTO.toString() : " + rDTO.toString());

        log.info(this.getClass().getName() + "마이페이지 수정페이지 보여주기 종료!");

        return "user/profileModify";
    }



    /**
     *회원 정보 수정 전 닉네임 중복체크하기(Ajax를 통해 입력한 닉네임 정보 받음)
     */
    @ResponseBody
    @PostMapping(value = "getNicknameExists")
    public UserInfoDTO getNicknameExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".controller 닉네임 중복체크 시작 !");

        String userNickname = CmmUtil.nvl(request.getParameter("userNickname")); // 회원아이디

        log.info("userNickname : " + userNickname);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserNickname(userNickname);

        // 회원아이디를 통해 중복된 아이디인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getNicknameExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".controller 닉네임 중복체크 끝 !");

        return rDTO;
    }

    /* 회원 정보 수정 로직 */
    @ResponseBody
    @PostMapping(value = "profileModifyProc")
    public MsgDTO updateProc(HttpSession session, HttpServletRequest request,
                             @RequestParam(value = "file", required = false) List<MultipartFile> files) throws Exception {

        log.info(this.getClass().getName() + "마이페이지 수정 시작!");

        String msg = "";
        int result = 0;
        MsgDTO rDTO = null;


        try {

            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String userNickname = CmmUtil.nvl(request.getParameter("userNickname"));
            String[] userInterestArray = request.getParameterValues("userInterest"); // 관심사 배열
            String userInterest = String.join(",", userInterestArray); // 배열을 쉼표로 구분된 문자열로 변환

            log.info("userId : " + userId);
            log.info("userNickame : " + userNickname);
            log.info("userInterest : " + userInterest);



            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserId(userId);
            pDTO.setUserNickname(userNickname);
            pDTO.setUserInterest(userInterest);


            profileService.updateProfile(pDTO);

            msg = "수정되었습니다.";
            result = 1;

            if (files != null) {

                // 기존에 있던 파일들 삭제
                fileService.deleteFile(pDTO);

                String saveFilePath = FileUtil.mkdirForData();      // 웹서버에 저장할 파일 경로 생성

                log.info("userId : " + userId);

                for (MultipartFile mf : files) {

                    log.info("mf : " + mf);

                    String orgFileName = mf.getOriginalFilename();      // 파일의 원본 명
                    String fileSize = String.valueOf(mf.getSize());     // 파일 크기
                    String ext = orgFileName.substring(orgFileName.lastIndexOf(".") + 1,    // 확장자
                            orgFileName.length()).toLowerCase();

                    // 이미지 파일만 실행되도록 함
                    if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("gif") || ext.equals("png")) {


                        log.info("userId : " + userId);
                        log.info("orgFileName : " + orgFileName);
                        log.info("fileSize : " + fileSize);
                        log.info("ext : " + ext);
                        log.info("saveFilePath : " + saveFilePath);

                        FileDTO fileDTO = new FileDTO();
                        fileDTO.setOrgFileName(orgFileName);
                        fileDTO.setFilePath(saveFilePath);
                        fileDTO.setFileSize(fileSize);
                        fileDTO.setUserId(userId);


                        FileDTO fDTO = s3Service.uploadFile(mf, ext);
                        fileDTO.setFileUrl(fDTO.getFileUrl());
                        fileDTO.setFileName(fDTO.getFileName());

                        log.info("sageFileUrl : " + fDTO.getFileUrl());

                        fileService.insertFile(fileDTO);

                        fileDTO = null;

                    }
                }
            }


        } catch (Exception e) {
            msg = "수정에 실패하였습니다. 다시 시도해주세요.";
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            rDTO = MsgDTO.builder().msg(msg).result(result).build();

            log.info(this.getClass().getName() + "마이페이지 수정 종료!");
        }

        return rDTO;
    }

    /* 회원탈퇴 ( cascade 설정으로 인한 코드 단순화 ) */
    @ResponseBody
    @PostMapping(value = "deleteUserInfo")
    public MsgDTO profileDelete(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".controller 회원탈퇴 시작 !");

        String msg = ""; // 메시지 내용
        MsgDTO rDTO = null; // 결과 메시지 구조

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            log.info("SS_USER_ID : " + userId);

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserId(userId);

            // 회원정보 삭제하기 메서드 호출 (CASCADE 설정이 되어 있을 경우)
            profileService.deleteUserInfo(pDTO);

            msg = "탈퇴되었습니다.";
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            rDTO = MsgDTO.builder().msg(msg).build();

            // 세션에 있는 유저아이디 삭제하기!
            session.removeAttribute("SS_USER_ID");

            log.info("세션 삭제 후 session.getAttribute(\"SS_USER_ID\") : " + session.getAttribute("SS_USER_ID"));

            log.info(this.getClass().getName() + ".controller 회원탈퇴 끝 !");
        }

        return rDTO;
    }

//    /* 회원 탈퇴 */
//    @ResponseBody
//    @PostMapping(value = "/deleteUserInfo")
//    public MsgDTO profileDelete(HttpServletRequest request, HttpSession session) throws Exception {
//
//        log.info(this.getClass().getName() + ".controller 회원탈퇴 시작 !");
//
//        String msg = ""; // 메시지 내용
//        MsgDTO rDTO = null; // 결과 메시지 구조
//
//        String fileSeq = CmmUtil.nvl((request.getParameter("fileSeq")));
//        String boardSeq = CmmUtil.nvl(request.getParameter("boardSeq"));
//        String commentSeq = CmmUtil.nvl(request.getParameter("commentSeq"));
//        String challengeSeq = CmmUtil.nvl(request.getParameter("challengeSeq"));
//        String checkSeq = CmmUtil.nvl(request.getParameter("checkSeq"));
//        String testSeq = CmmUtil.nvl(request.getParameter("testSeq"));
//
//
//        try {
//
//            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
//
//            log.info("SS_USER_ID : " + userId);
//            log.info("fileSeq : " + fileSeq);
//            log.info("boardSeq : " + boardSeq);
//            log.info("commentSeq : " + commentSeq);
//            log.info("challengeSeq : " + challengeSeq);
//            log.info("checkSeq : " + checkSeq);
//            log.info("testSeq : " + testSeq);
//
//            UserInfoDTO pDTO = new UserInfoDTO();
//            pDTO.setUserId(userId);
//            pDTO.setFileSeq(fileSeq);
//            pDTO.setBoardSeq(boardSeq);
//            pDTO.setCommentSeq(commentSeq);
//            pDTO.setChallengeSeq(challengeSeq);
//            pDTO.setCheckSeq(checkSeq);
//            pDTO.setTestSeq(testSeq);
//
//            // userId를 외래키로 참조하는 서비스들 삭제
//            fileService.deleteFile(pDTO);
//            boardService.deleteUserInfo(pDTO);
//
//            // 회원정보 삭제하기 메서드 호출
//            profileService.deleteUserInfo(pDTO);
//
//            msg = "탈퇴되었습니다.";
//
//        } catch (Exception e) {
//
//            msg = "실패하였습니다. : " + e.getMessage();
//            log.info(e.toString());
//            e.printStackTrace();
//
//        } finally {
//
//            rDTO = MsgDTO.builder().msg(msg).build();
//
//            // 세션에 있는 유저아이디 삭제하기!
//            session.removeAttribute("SS_USER_ID");
//
//            log.info("세션 삭제 후 session.getAttribute(\"SS_USER_ID\") : " + session.getAttribute("SS_USER_ID"));
//
//            log.info(this.getClass().getName() + ".controller 회원탈퇴 끝 !");
//
//        }
//
//        return rDTO;
//    }
}