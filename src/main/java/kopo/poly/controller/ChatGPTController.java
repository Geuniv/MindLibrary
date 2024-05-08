package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.dto.ChatCompletionDTO;
import kopo.poly.dto.ChatRequestMsgDTO;
import kopo.poly.service.impl.ChatGPTService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @GetMapping(value = "/mindCheck")
    public String mindCheck() throws Exception {

        log.info(this.getClass().getName() + ".mindCheck Start!");

        log.info(this.getClass().getName() + ".mindCheck End!");

        return "/check/mindCheck";
    }

    @ResponseBody
    @PostMapping("/prompt")
    public String prompt(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".prompt Start!");

        String content = CmmUtil.nvl(request.getParameter("content"));
//        String content = ("요즘 너무 우울해");

        ChatRequestMsgDTO pDTO = ChatRequestMsgDTO.builder().role("system").content(content).build();


        List<ChatRequestMsgDTO> rList = new ArrayList<>();
        rList.add(pDTO);

        ChatCompletionDTO rDTO = ChatCompletionDTO.builder().model("gpt-4-turbo").messages(rList).build();

        Map<String, Object> json = chatGPTService.prompt(rDTO);

        log.info(json.toString());

        JSONObject jsonObject = new JSONObject(json);
        JSONObject message = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message");

        String result = message.getString("content");

        log.info("result : " + result);


        log.info(this.getClass().getName() + ".prompt End!");


        return result;
    }

    @PostMapping(value = "promptResult")
    public String promptResult() throws Exception {
        log.info(this.getClass().getName() + ".promptResult Start!");

        log.info(this.getClass().getName() + ".promptResult End!");

        return "/check/mindCheckResult";
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
