package kopo.poly.controller;

import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IProfileService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/profile")
@RequiredArgsConstructor
@Controller
public class ProfileController {

    private final IProfileService profileService;

//    private final IUserInfoService userInfoService;

    @GetMapping(value = "")
    public String profile(ModelMap modelMap, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + "프로필 조회 컨트롤러 시작 !");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("세션에서 받아온 userId : " + userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        UserInfoDTO rDTO = Optional.ofNullable(profileService.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("DB에서 가져와 복호화 하기 전 이메일 : " + rDTO.getUserEmail());

        rDTO.setUserEmail(EncryptUtil.decAES128CBC(rDTO.getUserEmail()));

        log.info("DB에서 가져와 복호화 하고나서 이메일 : " + rDTO.getUserEmail());

        modelMap.addAttribute("rDTO", rDTO);

        log.info("회원정보 조회 rDTO.toString() : " + rDTO.toString());

        log.info(this.getClass().getName() + "프로필 조회 컨트롤러 끝 !");

        return "/user/profile";
    }


    @GetMapping(value = "/profileModify")
    public String profileModify(HttpSession session, ModelMap modelMap) throws Exception {

        log.info(this.getClass().getName() + ".profileModify Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("세션에서 받아온 userId : " + userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        log.info("pDTO : " + pDTO.toString());

        UserInfoDTO rDTO = Optional.ofNullable(profileService.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("DB에서 가져와 복호화 하기 전 이메일 : " + rDTO.getUserEmail());

        rDTO.setUserEmail(EncryptUtil.decAES128CBC(rDTO.getUserEmail()));

        log.info("DB에서 가져와 복호화 하고나서 이메일 : " + rDTO.getUserEmail());

        modelMap.addAttribute("rDTO", rDTO);

        log.info("회원정보 조회 rDTO.toString() : " + rDTO.toString());

        log.info(this.getClass().getName() + "profileModify End!");

        return "/user/profileModify";
    }


    @ResponseBody
    @PostMapping(value = "/profileModify/updateProc")
    public MsgDTO updateProc(HttpSession session, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".profileModify/updateProc Start!");

        String msg = "";
        int result = 0;
        MsgDTO rDTO = null;

        try {

            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String userPassword = CmmUtil.nvl(request.getParameter("userPassword"));
            String userName = CmmUtil.nvl(request.getParameter("userName"));
//            String addr1 = CmmUtil.nvl(request.getParameter("addr1"));
//            String addr2 = CmmUtil.nvl(request.getParameter("addr2"));

            log.info("userId : " + userId);
            log.info("userPassword : " + userPassword);
            log.info("userName : " + userName);
//            log.info("addr1 : " + addr1);
//            log.info("addr2 : " + addr2);

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserId(userId);
            pDTO.setUserPassword(EncryptUtil.encHashSHA256(userPassword));
            pDTO.setUserName(userName);
//            pDTO.setAddr1(addr1);
//            pDTO.setAddr2(addr2);

            profileService.updateProfile(pDTO);

            msg = "수정되었습니다.";
            result = 1;


        } catch (Exception e) {
            msg = "수정에 실패하였습니다. 다시 시도해주세요.";
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            rDTO = MsgDTO.builder().msg(msg).result(result).build();

            log.info(this.getClass().getName() + ".profileModify/updateProc End!");
        }

        return rDTO;
    }


    @ResponseBody
    @PostMapping(value = "/deleteUserInfo")
    public MsgDTO profileDelete(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".deleteUserInfo Start!");

        String msg = ""; // 메시지 내용
        MsgDTO rDTO = null; // 결과 메시지 구조

        try {

            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            log.info("SS_USER_ID : " + userId);

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserId(userId);

            // 회원정보 삭제하기 메서드 호출
            profileService.deleteUserInfo(pDTO);

            msg = "탈퇴되었습니다.";

        } catch (Exception e) {

            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            rDTO = MsgDTO.builder().msg(msg).build();

//            // 만약 access_Token이 존재하면, 카카오 로그아웃 메소드 호출하기!
//            if (session.getAttribute("access_Token") != null) {
//
//                // 카카오 로그아웃 메소드 호출
//                loginService.kakaoLogout((String) session.getAttribute("access_Token"));
//
//                // 세션에 있는 접근토큰 삭제하기!
//                session.removeAttribute("access_Token");
//            }

            // 세션에 있는 유저아이디 삭제하기!
            session.removeAttribute("SS_USER_ID");

            log.info("세션 삭제 후 session.getAttribute(\"SS_USER_ID\") : " + session.getAttribute("SS_USER_ID"));

            log.info(this.getClass().getName() + ".deleteUserInfo End!");

        }

        return rDTO;
    }

}