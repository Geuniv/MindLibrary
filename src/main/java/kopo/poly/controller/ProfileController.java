package kopo.poly.controller;

import kopo.poly.dto.BoardDTO;
import kopo.poly.dto.FileDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
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

    private final IProfileService profileService;

    private final IUserInfoService userInfoService;

    private final IFileService fileService;

    private final IS3Service s3Service;

    private final IBoardService boardService;

    @GetMapping(value = "")
    public String profile(ModelMap model, HttpSession session,
                          @RequestParam(defaultValue = "1") int page) throws Exception {

        log.info(this.getClass().getName() + "마이페이지 조회 컨트롤러 시작!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("SS_USER_ID : " + userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        UserInfoDTO rDTO = Optional.ofNullable(profileService.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("pDTO : " + pDTO);

        // 이미지 가져오기
        List<FileDTO> rList = Optional.ofNullable(fileService.getFile(pDTO)).orElseGet(ArrayList::new);

        log.info("rList : " + rList);

        model.addAttribute("rList", rList);

        log.info("rList : " + rList);

        // 이미지 가져오기 종료

        log.info("복호화 전 Email : " + rDTO.getUserEmail());

        rDTO.setUserEmail(EncryptUtil.decAES128CBC(rDTO.getUserEmail()));

        log.info("복호화 후 Email : " + rDTO.getUserEmail());

        model.addAttribute("rDTO", rDTO);

        log.info("회원정보 조회 rDTO.toString() : " + rDTO.toString());

        List<BoardDTO> userPosts = boardService.getPostsByUserId(userId);
        model.addAttribute("userPosts", userPosts);

        log.info("userPosts : " + userPosts);

        log.info(this.getClass().getName() + "마이페이지 조회 컨트롤러 종료!");

        return "user/profile";

    }


    @GetMapping(value = "/profileModify")
    public String profileModify(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + "마이페이지 수정페이지 보여주기 시작!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("세션에서 받아온 userId : " + userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        log.info("pDTO : " + pDTO.toString());

        UserInfoDTO rDTO = Optional.ofNullable(profileService.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        // 이미지 가져오기
        List<FileDTO> rList = Optional.ofNullable(fileService.getFile(pDTO)).orElseGet(ArrayList::new);

        log.info("rList : " + rList);

//        rList.add(rDTO.getImage(fDTO.getFileUrl()));
        model.addAttribute("rList", rList);

        log.info("rList : " + rList);

        // 이미지 가져오기 종료

        log.info("DB에서 가져와 복호화 하기 전 이메일 : " + rDTO.getUserEmail());

        rDTO.setUserEmail(EncryptUtil.decAES128CBC(rDTO.getUserEmail()));

        log.info("DB에서 가져와 복호화 하고나서 이메일 : " + rDTO.getUserEmail());

        model.addAttribute("rDTO", rDTO);

        log.info("회원정보 조회 rDTO.toString() : " + rDTO.toString());

        log.info(this.getClass().getName() + "마이페이지 수정페이지 보여주기 종료!");

        return "user/profileModify";
    }


    @ResponseBody
    @PostMapping(value = "/profileModifyProc")
    public MsgDTO updateProc(HttpSession session, HttpServletRequest request,
                             @RequestParam(value = "file", required = false) List<MultipartFile> files) {

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

    @ResponseBody
    @PostMapping(value = "/deleteUserInfo")
    public MsgDTO profileDelete(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".deleteUserInfo Start!");

        String msg = ""; // 메시지 내용
        MsgDTO rDTO = null; // 결과 메시지 구조

        String fileSeq = CmmUtil.nvl((request.getParameter("fileSeq")));
        String boardSeq = CmmUtil.nvl(request.getParameter("boardSeq"));
        String commentSeq = CmmUtil.nvl(request.getParameter("commentSeq"));
        String challengeSeq = CmmUtil.nvl(request.getParameter("challengeSeq"));
        String checkSeq = CmmUtil.nvl(request.getParameter("checkSeq"));
        String testSeq = CmmUtil.nvl(request.getParameter("testSeq"));


        try {

            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            log.info("SS_USER_ID : " + userId);
            log.info("fileSeq : " + fileSeq);
            log.info("boardSeq : " + boardSeq);
            log.info("commentSeq : " + commentSeq);
            log.info("challengeSeq : " + challengeSeq);
            log.info("checkSeq : " + checkSeq);
            log.info("testSeq : " + testSeq);

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserId(userId);
            pDTO.setFileSeq(fileSeq);
            pDTO.setBoardSeq(boardSeq);
            pDTO.setCommentSeq(commentSeq);
            pDTO.setChallengeSeq(challengeSeq);
            pDTO.setCheckSeq(checkSeq);
            pDTO.setTestSeq(testSeq);

            // userId를 외래키로 참조하는 서비스들 삭제
            fileService.deleteFile(pDTO);
            boardService.deleteUserInfo(pDTO);

            // 회원정보 삭제하기 메서드 호출
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

            log.info(this.getClass().getName() + ".deleteUserInfo End!");

        }

        return rDTO;
    }
}