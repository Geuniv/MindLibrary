package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.chat.ChatHandler;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.IUserInfoMapper;
import kopo.poly.service.IProfileService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.IUserInfoMapper;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/chat")
public class ChatController {

    private final IUserInfoService userInfoService;
    private final IProfileService profileService;
    private final IUserInfoMapper userInfoMapper;

    @GetMapping(value = "intro")
    public String intro(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + " 인트로 시작!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        if (userId.length() < 1) {
//            String msg = "로그인 정보가 없습니다. \n 로그인 후 이용해 주세요.";
            String msg = "로그인 후 이용해 주세요.";
            String url = "/user/login";

            model.addAttribute("msg", msg);
            model.addAttribute("url", url);

            return "redirect";
        }

        log.info(this.getClass().getName() + " 인트로 종료!");

        return "chat/intro";
    }

    @GetMapping(value = "chatroom")
    public String chatroom(@RequestParam String roomName, HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + " 채팅방 화면 시작!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("roomName : " + roomName);
        log.info("userId : " + userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        UserInfoDTO rDTO = Optional.ofNullable(profileService.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        String nickname = rDTO.getUserNickname(); // 세션의 닉네임 정보 사용

        model.addAttribute("nickname", nickname);
        model.addAttribute("roomName", roomName);

        log.info(this.getClass().getName() + " 채팅방 화면 종료!");

        return "chat/chatroom";
    }

    @RequestMapping(value = "roomList")
    @ResponseBody
    public Set<String> roomList() throws Exception {

        log.info(this.getClass().getName() + " 채팅방 리스트 시작!");

        log.info(this.getClass().getName() + " 채팅방 리스트 종료!");

        return ChatHandler.roomInfo.keySet();
    }

    @PostMapping(value = "createRoom")
    @ResponseBody
    public String createRoom(@RequestParam String roomName, HttpSession session) throws Exception {
        ChatHandler.roomInfo.putIfAbsent(roomName, roomName);
        return "success";
    }
}
