package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.*;
import kopo.poly.service.ICheckService;
import kopo.poly.service.impl.ChatGPTService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ChatGPT API
 *
 * @author : lee
 * @fileName : ChatGPTController
 * @since : 12/29/23
 */
@Slf4j
//@RestController
@RequestMapping(value = "/check") // /api/v1/chatGpt
@RequiredArgsConstructor
@Controller
public class ChatGPTController {

    private final ChatGPTService chatGPTService;

    private final ICheckService checkService;

    /**
     * [API] 최신 ChatGPT 프롬프트 명령어를 수행합니다. : gpt-4, gpt-4 turbo, gpt-3.5-turbo
     *
//     * @param chatCompletionDto
     * @return
     */
//    @PostMapping("/prompt")
//    public ResponseEntity<Map<String, Object>> selectPrompt(@RequestBody ChatCompletionDTO chatCompletionDto) {
//        log.debug("param :: " + chatCompletionDto.toString());
//        Map<String, Object> result = chatGPTService.prompt(chatCompletionDto);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }

    /* 마음체크 메인 페이지
    * ( 2024.05.08 완 )
    */
    @GetMapping(value = "/mindCheck")
    public String mindCheck() throws Exception {

        log.info(this.getClass().getName() + ".mindCheck Start!");

        log.info(this.getClass().getName() + ".mindCheck End!");

        return "check/mindCheck";
    }

    /* 마음체크 ChatGPT Api 호출 답변 받기
     * ( 2024.05.08 완 )
     */
    @ResponseBody
    @PostMapping("/prompt")
    public String prompt(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".prompt Start!");

        String userQuestion = CmmUtil.nvl(request.getParameter("userQuestion"));
//        String content = ("요즘 너무 우울해");

        ChatRequestMsgDTO pDTO = ChatRequestMsgDTO.builder().role("system").content(userQuestion).build();


        List<ChatRequestMsgDTO> rList = new ArrayList<>();
        rList.add(pDTO);

        ChatCompletionDTO rDTO = ChatCompletionDTO.builder().model("gpt-4-turbo").messages(rList).build();

        Map<String, Object> json = chatGPTService.prompt(rDTO);

        log.info(json.toString());

        JSONObject jsonObject = new JSONObject(json);
        JSONObject message = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message");

        String result = message.getString("content");

        log.info("result : " + result);

        // 모델에 userAnswer 추가
        model.addAttribute("result", result);

        log.info(this.getClass().getName() + ".prompt End!");


        return result;
    }

    /* 마음체크 결과 저장하기
     * ( 2024.05.09 완 )
     */
    @ResponseBody
    @PostMapping(value = "insertCheckInfo")
    public MsgDTO insertCheckInfo(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".insertCheckInfo Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            // 로그인된 사용자 아이디 가져오기
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String userQuestion = CmmUtil.nvl(request.getParameter("userQuestion")); // 질문 내용
            String userAnswer = CmmUtil.nvl(request.getParameter("userAnswer")); // 답변 내용


            log.info("ss_user_id : " + userId);
            log.info("userQuestion : " + userQuestion);
            log.info("userAnswer : " + userAnswer);

            // 데이터 저장하기 위해 DTO에 저장하기
            CheckDTO pDTO = CheckDTO.builder()
                                                            .userId(userId)
                                                            .userQuestion(userQuestion)
                                                            .userAnswer(userAnswer)
                                                            .build();

            // 게시글 등록하기 위한 비즈니스 로직 호출
            checkService.insertCheckInfo(pDTO);

            // 저장이 완료되면 사용자에게 보여줄 메시지
            msg = "결과가 저장되었습니다.";

        } catch (Exception e) {

            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".insertCheckInfo End!");
        }
        return dto;
    }

    /* 마음체크 결과 페이지
     * ( 2024.05.09 완 )
     */
    @GetMapping(value = "promptResult")
    public String promptResult(ModelMap model, HttpSession session,
                          @RequestParam(defaultValue = "1") int page) throws Exception {

        log.info(this.getClass().getName() + "마음체크 결과 컨트롤러 시작!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("SS_USER_ID : " + userId);

        CheckDTO pDTO = new CheckDTO();
        pDTO.setUserId(userId);

        CheckDTO rDTO = Optional.ofNullable(checkService.getCheckInfo(pDTO)).orElseGet(CheckDTO::new);

        log.info("pDTO : " + pDTO);

        model.addAttribute("rDTO", rDTO);

        log.info("마음체크 결과 rDTO.toString() : " + rDTO.toString());

        log.info(this.getClass().getName() + "마음체크 결과 컨트롤러 종료!");

        return "check/promptResult";

    }

/*-----------------------------------------------------------------------------------------------*/

//    public ChatGPTController(ChatGPTService chatGPTService) {
//        this.chatGPTService = chatGPTService;
//    }

    /**
     * [API] ChatGPT 모델 리스트를 조회합니다.
     */
//    @GetMapping("/modelList")
//    public ResponseEntity<List<Map<String, Object>>> selectModelList() {
//        List<Map<String, Object>> result = chatGPTService.modelList();
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }

    /**
     * [API] ChatGPT 유효한 모델인지 조회합니다.
     *
     * @param modelName
     * @return
     */
//    @GetMapping("/model")
//    public ResponseEntity<Map<String, Object>> isValidModel(@RequestParam(name = "modelName") String modelName) {
//        Map<String, Object> result = chatGPTService.isValidModel(modelName);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }

    /**
     * [API] Legacy ChatGPT 프롬프트 명령을 수행합니다. : gpt-3.5-turbo-instruct, babbage-002, davinci-002
     *
     * @param completionDto {}
     * @return ResponseEntity<Map < String, Object>>
     */
//    @PostMapping("/legacyPrompt")
//    public ResponseEntity<Map<String, Object>> selectLegacyPrompt(@RequestBody CompletionDto completionDto) {
//        log.debug("param :: " + completionDto.toString());
//        Map<String, Object> result = chatGPTService.legacyPrompt(completionDto);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }

}
