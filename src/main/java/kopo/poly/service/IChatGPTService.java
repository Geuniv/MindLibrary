package kopo.poly.service;

import kopo.poly.dto.ChatCompletionDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ChatGPT 서비스 인터페이스
 *
 * @author : lee
 * @fileName : ChatGPTService
 * @since : 12/29/23
 */

@Service
public interface IChatGPTService {

//    List<Map<String, Object>> modelList();
//
//    Map<String, Object> isValidModel(String modelName);

    Map<String, Object> prompt(ChatCompletionDTO chatCompletionDto);
}
