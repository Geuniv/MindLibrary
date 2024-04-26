package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
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

import java.util.Optional;

@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Controller
public class UserInfoController {

    private final IUserInfoService userInfoService;

    /**
     * 회원가입 화면으로 이동
     */
    @GetMapping(value = "userRegForm")
    public String userRegForm() {
        log.info(this.getClass().getName() + ".user/userRegForm");

        return "/user/userRegForm";
    }

    /**
     * 회원 가입 전 아이디 중복체크하기(Ajax를 통해 입력한 아이디 정보 받음)
     */
    @ResponseBody
    @PostMapping(value = "getUserIdExists")
    public UserInfoDTO getUserExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        String userId = CmmUtil.nvl(request.getParameter("userId")); // 회원아이디

        log.info("userId : " + userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        // 회원아이디를 통해 중복된 아이디인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserIdExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getUserIdExists End!");

        return rDTO;
    }

    /**
     * 회원 가입 전 이메일 중복체크하기(Ajax를 통해 입력한 아이디 정보 받음)
     * 유효한 이메일인 확인하기 위해 입력된 이메일에 인증번호 포함하여 메일 발송
     */
    @ResponseBody
    @PostMapping(value = "getEmailExists")
    public UserInfoDTO getEmailExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getEmailExists Start!");

        String userEmail = CmmUtil.nvl(request.getParameter("userEmail")); // 회원이메일

        log.info("userEmail : " + userEmail);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserEmail(EncryptUtil.encAES128CBC(userEmail));

        // 입력된 이메일이 중복된 이메일인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getEmailExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getEmailExists End!");

        return rDTO;
    }

    /**
     * 회원가입 로직 처리
     */
    @ResponseBody
    @PostMapping(value = "insertUserInfo")
    public MsgDTO insertUserInfo(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo start!");

        int res = 0; // 회원가입 결과
        String msg = ""; //회원가입 결과에 대한 메시지를 전달할 변수
        MsgDTO dto = null; // 결과 메시지 구조

        //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO = null;

        try {

            /*
             * #######################################################
             *        웹(회원정보 입력화면)에서 받는 정보를 String 변수에 저장 시작!!
             *
             *    무조건 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
             * #######################################################
             */
            String userId = CmmUtil.nvl(request.getParameter("userId")); //아이디
            String userPassword = CmmUtil.nvl(request.getParameter("userPassword")); //비밀번호
            String userEmail = CmmUtil.nvl(request.getParameter("userEmail")); //이메일
            String userName = CmmUtil.nvl(request.getParameter("userName")); //이름
            String userNickname = CmmUtil.nvl(request.getParameter("userNickname")); //닉네임
            String userAge = CmmUtil.nvl(request.getParameter("userAge")); //나이
            String userGender = CmmUtil.nvl(request.getParameter("userGender")); //성별
            String[] userInterestArray = request.getParameterValues("userInterest"); // 관심사 배열
            String userInterest = String.join(",", userInterestArray); // 배열을 쉼표로 구분된 문자열로 변환
            /*
             * #######################################################
             *        웹(회원정보 입력화면)에서 받는 정보를 String 변수에 저장 끝!!
             *
             *    무조건 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
             * #######################################################
             */

            /*
             * #######################################################
             * 	 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함
             * 						반드시 작성할 것
             * #######################################################
             * */
            log.info("userId : " + userId);
            log.info("userPassword : " + userPassword);
            log.info("userEmail : " + userEmail);
            log.info("userName : " + userName);
            log.info("userNickname : " + userNickname);
            log.info("userAge : " + userAge);
            log.info("userGender : " + userGender);
            log.info("userInterest : " + userInterest);

            /*
             * #######################################################
             *        웹(회원정보 입력화면)에서 받는 정보를 DTO에 저장하기 시작!!
             *
             *        무조건 웹으로 받은 정보는 DTO에 저장해야 한다고 이해하길 권함
             * #######################################################
             */

            //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUserId(userId);

            //비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화함
            pDTO.setUserPassword(EncryptUtil.encHashSHA256(userPassword));

            //민감 정보인 이메일은 AES128-CBC로 암호화함
            pDTO.setUserEmail(EncryptUtil.encAES128CBC(userEmail));

            pDTO.setUserName(userName);
            pDTO.setUserNickname(userNickname);
            pDTO.setUserAge(userAge);
            pDTO.setUserGender(userGender);
            pDTO.setUserInterest(userInterest);

            /*
             * #######################################################
             *        웹(회원정보 입력화면)에서 받는 정보를 DTO에 저장하기 끝!!
             *
             *        무조건 웹으로 받은 정보는 DTO에 저장해야 한다고 이해하길 권함
             * #######################################################
             */

            /*
             * 회원가입
             * */
            res = userInfoService.insertUserInfo(pDTO);

            log.info("회원가입 결과(res) : " + res);

            if (res == 1) {
                msg = "회원가입되었습니다.";

                //추후 회원가입 입력화면에서 ajax를 활용해서 아이디 중복, 이메일 중복을 체크하길 바람
            } else if (res == 2) {
                msg = "이미 가입된 아이디입니다.";

            } else {
                msg = "오류로 인해 회원가입이 실패하였습니다.";

            }

        } catch (Exception e) {
            //저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            // 결과 메시지 전달하기
            dto = MsgDTO.builder().result(res).msg(msg).build();

            log.info(this.getClass().getName() + ".insertUserInfo End!");
        }

        return dto;
    }

    /**
     * 로그인을 위한 입력 화면으로 이동
     */
    @GetMapping(value = "login")
    public String login() {
        log.info(this.getClass().getName() + ".user/login Start!");

        log.info(this.getClass().getName() + ".user/login End!");

        return "user/login";
    }

    /**
     * 로그인 처리 및 결과 알려주는 화면으로 이동
     */
    @ResponseBody
    @PostMapping(value = "loginProc")
    public MsgDTO loginProc(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".loginProc Start!");

        int res = 0; //로그인 처리 결과를 저장할 변수 (로그인 성공 : 1, 아이디, 비밀번호 불일치로인한 실패 : 0, 시스템 에러 : 2)
        String msg = ""; //로그인 결과에 대한 메시지를 전달할 변수
        MsgDTO dto = null; // 결과 메시지 구조

        //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO = null;

        try {

            String userId = CmmUtil.nvl(request.getParameter("userId")); //아이디
            String userPassword = CmmUtil.nvl(request.getParameter("userPassword")); //비밀번호

            log.info("userId : " + userId);
            log.info("userPassword : " + userPassword);

            //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUserId(userId);

            //비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화함
            pDTO.setUserPassword(EncryptUtil.encHashSHA256(userPassword));

            // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기 위한 userInfoService 호출하기
            UserInfoDTO rDTO = userInfoService.getLogin(pDTO);

            /*
             * 로그인을 성공했다면, 회원아이디 정보를 session에 저장함
             *
             * 세션은 톰켓(was)의 메모리에 존재하며, 웹사이트에 접속한 사람(연결된 객체)마다 메모리에 값을 올린다.
             * 			 *
             * 예) 톰켓에 100명의 사용자가 로그인했다면, 사용자 각각 회원아이디를 메모리에 저장하며.
             *    메모리에 저장된 객체의 수는 100개이다.
             *    따라서 과도한 세션은 톰켓의 메모리 부하를 발생시켜 서버가 다운되는 현상이 있을 수 있기때문에,
             *    최소한으로 사용하는 것을 권장한다.
             *
             * 스프링에서 세션을 사용하기 위해서는 함수명의 파라미터에 HttpSession session 존재해야 한다.
             * 세션은 톰켓의 메모리에 저장되기 때문에 url마다 전달하는게 필요하지 않고,
             * 그냥 메모리에서 부르면 되기 때문에 jsp, controller에서 쉽게 불러서 쓸수 있다.
             * */
            if (CmmUtil.nvl(rDTO.getUserId()).length() > 0) { //로그인 성공

                res = 1;
                /*
                 * 세션에 회원아이디 저장하기, 추후 로그인여부를 체크하기 위해 세션에 값이 존재하는지 체크한다.
                 * 일반적으로 세션에 저장되는 키는 대문자로 입력하며, 앞에 SS를 붙인다.
                 *
                 * Session 단어에서 SS를 가져온 것이다.
                 */
                msg = "로그인이 성공했습니다.";

                session.setAttribute("SS_USER_ID", userId);
                session.setAttribute("SS_USER_NAME", CmmUtil.nvl(rDTO.getUserName()));

            } else {
                msg = "아이디와 비밀번호가 올바르지 않습니다.";

            }

        } catch (Exception e) {
            //저장이 실패되면 사용자에게 보여줄 메시지
            msg = "시스템 문제로 로그인이 실패했습니다.";
            res = 2;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            // 결과 메시지 전달하기
            dto = MsgDTO.builder().result(res).msg(msg).build();
//            dto.setResult(res);
//            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".loginProc End!");
        }

        return dto;
    }

    /**
     * 로그인 성공 페이지 이동
     */
    @GetMapping(value = "loginResult")
    public String loginSuccess() {
        log.info(this.getClass().getName() + ".user/loginResult Start!");

        log.info(this.getClass().getName() + ".user/loginResult End!");

        return "user/loginResult";
    }

    /**
     * 아아디 찾기 화면
     */
    @GetMapping(value = "findUserId")
    public String findUserId() {
        log.info(this.getClass().getName() + ".user/findUserId Start!");

        log.info(this.getClass().getName() + ".user/findUserId End!");

        return "user/findUserId";

    }

    /**
     * 아아디 찾기 로직 수행
     */
    @PostMapping(value = "findUserIdProc")
    public String findUserIdProc(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".user/findUserIdProc Start!");

        int res = 0; //로그인 처리 결과를 저장할 변수 (로그인 성공 : 1, 아이디, 비밀번호 불일치로인한 실패 : 0, 시스템 에러 : 2)
        String msg = ""; //로그인 결과에 대한 메시지를 전달할 변수
        MsgDTO dto = null; // 결과 메시지 구조
        String url = "/index";

        /*
         * ########################################################################
         *        웹(회원정보 입력화면)에서 받는 정보를 String 변수에 저장!!
         *
         *    무조건 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
         * ########################################################################
         */

        String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
        String userEmail = CmmUtil.nvl(request.getParameter("userEmail")); // 이메일

        /*
         * ########################################################################
         * 	 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함
         * 						반드시 작성할 것
         * ########################################################################
         * */
        log.info("userName : " + userName);
        log.info("userEmail : " + userEmail);

        /*
         * ########################################################################
         *        웹(회원정보 입력화면)에서 받는 정보를 DTO에 저장하기!!
         *
         *        무조건 웹으로 받은 정보는 DTO에 저장해야 한다고 이해하길 권함
         * ########################################################################
         */

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserName(userName);
        pDTO.setUserEmail(EncryptUtil.encAES128CBC(userEmail));

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.findUserIdOrPasswordProc(pDTO))
                .orElseGet(UserInfoDTO::new);

        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".user/findUserIdProc End!");

        return "user/findUserIdResult";

    }


    /**
     * 비밀번호 찾기 화면
     */
    @GetMapping(value = "findPassword")
    public String findPassword(HttpSession session) {
        log.info(this.getClass().getName() + ".user/findPassword Start!");

        // 강제 URL 입력 등 오는 경우가 있어 세션 삭제
        // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
        session.setAttribute("NEW_PASSWORD", "");
        session.removeAttribute("NEW_PASSWORD");

        log.info(this.getClass().getName() + ".user/findPassword End!");

        return "user/findPassword";

    }

    /**
     * 비밀번호 찾기 로직 수행
     * <p>
     * 아이디, 이름, 이메일 일치하면, 비밀번호 재발급 화면 이동
     */
    @PostMapping(value = "findPasswordProc")
    public String findPasswordProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".user/findPasswordProc Start!");

        /*
         * ########################################################################
         *        웹(회원정보 입력화면)에서 받는 정보를 String 변수에 저장!!
         *
         *    무조건 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
         * ########################################################################
         */

        String userId = CmmUtil.nvl(request.getParameter("userId")); // 아이디
        String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
        String userEmail = CmmUtil.nvl(request.getParameter("userEmail")); // 이메일

        /*
         * ########################################################################
         * 	 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함
         * 						반드시 작성할 것
         * ########################################################################
         * */
        log.info("userId : " + userId);
        log.info("userName : " + userName);
        log.info("userEmail : " + userEmail);

        /*
         * ########################################################################
         *        웹(회원정보 입력화면)에서 받는 정보를 DTO에 저장하기!!
         *
         *        무조건 웹으로 받은 정보는 DTO에 저장해야 한다고 이해하길 권함
         * ########################################################################
         */

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);
        pDTO.setUserName(userName);
        pDTO.setUserEmail(EncryptUtil.encAES128CBC(userEmail));

        // 비밀번호 찾기 가능한지 확인하기
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.findUserIdOrPasswordProc(pDTO)).orElseGet(UserInfoDTO::new);

        model.addAttribute("rDTO", rDTO);

        // 비밀번호 재생성하는 화면은 보안을 위해 반드시 NEW_PASSWORD 세션이 존재해야 접속 가능하도록 구현
        // userId 값을 넣은 이유는 비밀번호 재설정하는 newPasswordProc 함수에서 사용하기 위함
        session.setAttribute("NEW_PASSWORD", userId);

        log.info(this.getClass().getName() + ".user/findPasswordProc End!");

        return "user/newPassword";

    }

    /**
     * 비밀번호 찾기 로직 수행
     * <p>
     * 아이디, 이름, 이메일 일치하면, 비밀번호 재발급 화면 이동
     */
    @PostMapping(value = "newPasswordProc")
    public String newPasswordProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".user/newPasswordProc Start!");

        String msg = ""; // 웹에 보여줄 메시지

        // 정상적인 접근인지 체크
        String newPassword = CmmUtil.nvl((String) session.getAttribute("NEW_PASSWORD"));

        if (newPassword.length() > 0) { //정상 접근

            /*
             * ########################################################################
             *        웹(회원정보 입력화면)에서 받는 정보를 String 변수에 저장!!
             *
             *    무조건 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
             * ########################################################################
             */

            String userPassword = CmmUtil.nvl(request.getParameter("userPassword")); // 신규 비밀번호

            /*
             * ########################################################################
             * 	 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함
             * 						반드시 작성할 것
             * ########################################################################
             * */
            log.info("userPassword : " + userPassword);

            /*
             * ########################################################################
             *        웹(회원정보 입력화면)에서 받는 정보를 DTO에 저장하기!!
             *
             *        무조건 웹으로 받은 정보는 DTO에 저장해야 한다고 이해하길 권함
             * ########################################################################
             */

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserId(newPassword);
            pDTO.setUserPassword(EncryptUtil.encHashSHA256(userPassword));

            userInfoService.newPasswordProc(pDTO);

            // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
            session.setAttribute("NEW_PASSWORD", "");
            session.removeAttribute("NEW_PASSWORD");

            msg = "비밀번호가 재설정되었습니다.";

        } else { // 비정상 접근
            msg = "비정상 접근입니다.";
        }

        model.addAttribute("msg", msg);


        log.info(this.getClass().getName() + ".user/newPasswordProc End!");

        return "user/newPasswordResult";

    }

    /**
     * 로그아웃 처리
     * */
    @RequestMapping(value="/logout")
    public String logout(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".logout Start!");

        // 세션에 있는 유저아이디 삭제하기!
        session.removeAttribute("SS_USER_ID");

        log.info("세션 삭제 후 session.getAttribute(\"SS_USER_ID\") : " + session.getAttribute("SS_USER_ID"));

        log.info(this.getClass().getName() + ".logout End!");

        return "/index";
    }

    /*  회원 탈퇴 실행 로직  */
//    @PostMapping(value = "delete")
//    public String userDelete(ModelMap modelMap, HttpServletRequest request, HttpSession session) throws Exception {
//
//        log.info(this.getClass().getName() + ".controller 회원 탈퇴 실행");
//
//        String msg = "";
//        String url = "/index";
//
//        try {
//
//            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
////            String password = CmmUtil.nvl(request.getParameter("userDeletePw"));
//
//            log.info("탈퇴할 회원 ID : " + userId);
////            log.info("입력한 password : " + password);
//
//            UserInfoDTO pDTO = new UserInfoDTO();
//            pDTO.setUserId(userId);
////            pDTO.setPassword(EncryptUtil.encHashSHA256(password));
//
//            UserInfoDTO rDTO = userInfoService.getLogin(pDTO);
//
//            if (CmmUtil.nvl(rDTO.getUserId()).length() > 0) {
//                /*  회원 삭제용 비즈니스 로직 호출(쿼리문)  */
//                userInfoService.deleteUser(pDTO);
//
//                msg = "탈퇴되었습니다.";
//
//                session.invalidate();
//            }
//
//        } catch (Exception e) {
//
//            msg = "탈퇴에 실패하였습니다.";
//            url = "/user/myPage";
//
//            /*  실패 사유 확인용 로그  */
//            log.info(e.toString());
//            e.printStackTrace();    // Exception 발생 이유와 위치는 어디에서 발생했는지 전체적인 단계 출력
//
//        } finally {
//
//            modelMap.addAttribute("msg", msg);
//            modelMap.addAttribute("url", url);
//
//            log.info(this.getClass().getName() + ".controller 회원 탈퇴 종료");
//
//        }
//
//        return "/redirect";
//    }
}