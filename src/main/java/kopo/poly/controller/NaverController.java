package kopo.poly.controller;

import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.FileDTO;
import kopo.poly.dto.NaverDTO;
import kopo.poly.dto.TokenDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IFileService;
import kopo.poly.service.INaverService;
import kopo.poly.service.IS3Service;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Controller
public class NaverController {

    private final INaverService naverService; // 네이버 서비스 사용

    private final IUserInfoService userInfoService; // 유저 서비스 사용

    private final IS3Service s3Service; // 프로필 사진 가져와서 저장하기 위한 S3 서비스 사용

    private final IFileService fileService; // 프로필 사진 가져와서 저장하기 위한 파일 서비스 사용

    /* 네이버 로그인 엑세스 토큰 받기 */
    /* 프로필 사진 저장 로직 추가 ( 네이버 로그인 API - 2024.10.07 ) */
    @GetMapping(value = "/auth/naver/callback")
    public String naverCallback(String code, HttpSession session, ModelMap modelMap) throws Exception {

        log.info(".controller 네이버 회원가입 및 로그인 실행");
        log.info("콜백 컨트롤러 들어와서 매개변수로 받은 code 확인! : " + code);

        String msg = "";
        String url = "";
        int res; // 회원 가입 결과 /// 1 성공, 2 이미 가입

        // 네이버 서비스를 통해 엑세스 토큰 받아오기
        TokenDTO tokenDTO = naverService.getAccessToken(code);
        log.info("네이버 엑세스 토큰 : " + tokenDTO.getAccess_token());

        // 네이버 서비스를 통해 네이버 사용자 정보 가져오기
        NaverDTO naverDTO = naverService.getNaverUserInfo(tokenDTO);

        // 네이버 아이디 생성 및 네이버 사용자 이름 가져오기
        String userId = "naver_" + naverDTO.getResponse().getId();
        String userName = naverDTO.getResponse().getName();
        String profileImageUrl = naverDTO.getResponse().getProfileImage(); // 프로필 이미지 URL 가져오기 ( 2024.10.07 )

        log.info("네이버 아이디 : " + naverDTO.getResponse().getId());
        log.info("네이버 프로필 이미지 URL : " + profileImageUrl); // 프로필 이미지 URL 가져오기 ( 2024.10.07 )

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        UserInfoDTO rDTO = naverService.getUserInfoById(pDTO);

        // 첫 로그인 시 회원가입 로직 실행
        if (rDTO == null) {
            String email = naverDTO.getResponse().getEmail();
            String gender = naverDTO.getResponse().getGender();
            String nickname = naverDTO.getResponse().getNickname();
            String age = naverDTO.getResponse().getBirthyear() + "-" + naverDTO.getResponse().getBirthday();
            String interest = ""; // 관심사의 경우 네이버에 정보가 없기 떄문에 빈 값으로 초기화

            // 성별 설정
            if (Objects.equals(gender, "F")) {
                gender = "여자";

            } else if (Objects.equals(gender, "M")) {
                gender = "남자";

            }

            log.info("네이버 아이디(문자) : " + userId);
            log.info("네이버 이름 : " + userName);
            log.info("네이버 이메일 : " + email);
            log.info("네이버 성별 : " + gender);
            log.info("네이버 별명 : " + nickname);
            log.info("네이버 생년월일 : " + age);
            log.info("흥미 : " + interest);

            pDTO.setUserId(userId);
            pDTO.setUserName(userName);
            pDTO.setUserPassword(EncryptUtil.encHashSHA256(userId)); // 유저아이디를 해시로 암호화해서 비밀번호로 저장
            pDTO.setUserEmail(EncryptUtil.encAES128CBC(email));
            pDTO.setUserGender(gender);
            pDTO.setUserNickname(nickname);
            pDTO.setUserAge(age);
            pDTO.setUserInterest(interest);

            // 회원 가입
            res = userInfoService.insertUserInfo(pDTO);

            if (res == 1) {
                log.info("회원가입 성공");

                session.setAttribute("SS_USER_ID", userId);
                session.setAttribute("NAVER_ACCESS_TOKEN", tokenDTO.getAccess_token()); // 네이버 엑세스 토큰 DB에 저장하지 않고 Session ( 세션에 등록 )

                // 프로필 사진 저장 로직 추가
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    saveProfileImage(userId, profileImageUrl);
                }

                msg = "회원가입 및 로그인 성공! " + userName + "님 환영합니다.";
                url = "/index";

            } else {
                log.info("회원가입 실패");
                msg = "회원가입 실패하였습니다. 다시 시도해 주세요.";
                url = "/user/login";
            }

        } else {
            // 이미 계정 보유 시 로그인 처리
            log.info("계정 보유로 로그인 실행");

            session.setAttribute("SS_USER_ID", userId);
            session.setAttribute("NAVER_ACCESS_TOKEN", tokenDTO.getAccess_token());  // 네이버 엑세스 토큰 DB에 저장하지 않고 Session ( 세션에 등록 )

            msg = "로그인 성공! " + userName + "님 환영합니다.";
            url = "/index";
        }

        modelMap.addAttribute("msg", msg);
        modelMap.addAttribute("url", url);

        log.info(".controller 네이버 회원가입 및 로그인 종료");

        return "redirect";
    }

    /**
     * 프로필 사진 저장 로직 ( 2024.10.07 )
     * @param userId 사용자 아이디
     * @param profileImageUrl 프로필 이미지 URL
     */
    private void saveProfileImage(String userId, String profileImageUrl) {
        try {
            log.info("프로필 사진 저장 시작!");

            // URL에서 확장자 추출
            String extension = extractFileExtension(profileImageUrl);
            if (extension == null) {
                log.error("프로필 이미지 URL에서 확장자를 추출할 수 없습니다.");
                return;
            }

            // S3에 프로필 이미지 업로드
            FileDTO fileDTO = s3Service.uploadFileFromUrl(profileImageUrl, extension); // 추출한 확장자를 사용
            fileDTO.setUserId(userId);

            // 파일 정보를 DB에 저장하기
            fileService.insertFile(fileDTO);

            log.info("프로필 이미지 저장 URL : " + fileDTO.getFileUrl());
            log.info("프로필 이미지 파일 이름 : " + fileDTO.getFileName());

        } catch (Exception e) {
            log.error("프로필 사진 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * URL에서 파일 확장자를 추출하는 메서드
     * 프로필 사진 저장을 위한 로직 ( 2024.10.07 )
     * @param url 파일 URL
     * @return 추출된 파일 확장자 (예: "jpg", "jpeg", "png")
     */
    private String extractFileExtension(String url) {

        // 파일 확장자를 추출하기 위해 마지막 '.' 이후의 문자열을 가져옴
        if (url != null && url.contains(".")) {

            // 확장자 추출
            String extension = url.substring(url.lastIndexOf(".") + 1).toLowerCase();

            // 허용된 확장자 목록
            List<String> allowedExtensions = List.of("jpg", "jpeg", "png");

            // 허용된 확장자만 반환
            if (allowedExtensions.contains(extension)) {
                return extension;
            }
        }

        return null; // 허용되지 않은 확장자거나 확장자 추출 실패 시 null 반환
    }


    
//    /* 네이버 로그인 엑세스 토큰 받기 */
//    ★ 프로필 사진 가져오지 않는 원래 코드 ( 2024.10.01 ) ☆
//    @GetMapping(value = "/auth/naver/callback")
//    public String naverCallback(String code, HttpSession session, ModelMap modelMap) throws Exception {
//
//        log.info(".controller 네이버 회원가입 및 로그인 실행");
//        log.info("콜백 컨트롤러 들어와서 매개변수로 받은 code 확인! : " + code);
//
//        String msg = "";
//        String url = "";
//        int res; // 회원 가입 결과 /// 1 성공, 2 이미 가입
//
//        // 네이버 서비스를 통해 엑세스 토큰 받아오기
//        TokenDTO tokenDTO = naverService.getAccessToken(code);
//
//        log.info("네이버 엑세스 토큰 : " + tokenDTO.getAccess_token());
//
//        // 네이버 서비스를 통해 네이버 사용자 정보 가져오기
//        NaverDTO naverDTO = naverService.getNaverUserInfo(tokenDTO);
//
//        // 네이버 아이디 생성 및 네이버 사용자 이름 가져오기
//        String userId = "naver_" + naverDTO.getResponse().getId();
//        String userName = naverDTO.getResponse().getName();
//
//        log.info("네이버 아이디 : " + naverDTO.getResponse().getId());
//
//        UserInfoDTO pDTO = new UserInfoDTO();
//        pDTO.setUserId(userId);
//
//
//        UserInfoDTO rDTO = naverService.getUserInfoById(pDTO);
//
//        // 첫 로그인시 회원가입 로직 실행
//        if (rDTO == null) {
//
////            String profileImage = naverDTO.getResponse().getProfileImage();
//            String email = naverDTO.getResponse().getEmail();
//            String gender = naverDTO.getResponse().getGender();
//            String nickname = naverDTO.getResponse().getNickname();
//            String age = naverDTO.getResponse().getBirthyear() + "-" + naverDTO.getResponse().getBirthday();
//            String interest = ""; // 관심사의 경우 네이버에 정보가 없기 떄문에 빈 값으로 초기화
//
//            if (Objects.equals(gender, "F")) {
////                gender = "female";
//                gender = "여자";
//
//            } else if (Objects.equals(gender, "M")) {
////                gender = "male";
//                gender = "남자";
//
//            }
//
//            log.info("네이버 아이디(문자) : " + userId);
//            log.info("네이버 이름 : " + userName);
//            log.info("네이버 이메일 : " + email);
////            log.info("네이버 프사 : " + profileImage); 처리중
//            log.info("네이버 성별 : " + gender);
//            log.info("네이버 별명 : " + nickname);
//            log.info("네이버 생년월일 : " + age);
//            log.info("흥미 : " + interest);
//
//            pDTO.setUserId(userId);
//            pDTO.setUserName(userName);
//            pDTO.setUserPassword(EncryptUtil.encHashSHA256(userId)); // 오 유저아이디를 해시로 암호화 해서 비밀번호로 저장하는구나
//            pDTO.setUserEmail(EncryptUtil.encAES128CBC(email));
//            pDTO.setUserGender(gender);
//            pDTO.setUserNickname(nickname);
//            pDTO.setUserAge(age);
//            pDTO.setUserInterest(interest);
//
//            res = userInfoService.insertUserInfo(pDTO);
//
//            if (res == 1) {
//                log.info("회원가입 성공");
//
//                session.setAttribute("SS_USER_ID", userId);
//
//                msg = "회원가입 및 로그인 성공! " + userName + "님 환영합니다.";
//                url = "/index";
//
//            } else {
//                log.info("회원가입 실패");
//
//            }
//
//        } else {
//            // 이미 계정 보유 시 로그인 처리
//            log.info("계정 보유로 로그인 실행");
//
//            session.setAttribute("SS_USER_ID", userId);
//
//            msg = "로그인 성공! " + userName + "님 환영합니다.";
//            url = "/index";
//
//        }
//
//        modelMap.addAttribute("msg", msg);
//        modelMap.addAttribute("url", url);
//
//        log.info(".controller 네이버 회원가입 및 로그인 종료");
//
//        return "redirect";
//    }
}
