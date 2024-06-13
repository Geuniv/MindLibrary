package kopo.poly.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.ChatDTO;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatHandler extends TextWebSocketHandler {

    // 웹 소켓에 접속되는 사용자들을 저장하며, 중복을 제거하기 위해 Set 데이터 구조 사용함
    private static Set<WebSocketSession> clients = Collections.synchronizedSet(new LinkedHashSet<>());

    // 채팅을 조회하기 위해 사용
    public static Map<String, String> roomInfo = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * 클라이언트가 접속 시 호출되는 메서드
     *
     * @param session
     * @throws Exception
     * */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        log.info(this.getClass().getName() + ".채팅 접속 핸들러 시작!");

        String roomName = CmmUtil.nvl((String) session.getAttributes().get("roomName"));
        String nickname = CmmUtil.nvl((String) session.getAttributes().get("nickname"));
        String roomNameHash = CmmUtil.nvl((String) session.getAttributes().get("roomNameHash"));

        log.info("roomName : " + roomName);
        log.info("nickname : " + nickname);
        log.info("roomNameHash : " + roomNameHash);

        // 웹소켓에 접속된 모든 사용자 검색
        clients.forEach(s -> {

            // 내가 접속한 채팅방에 있는 세션만 메시지 보내기
            if (roomNameHash.equals(s.getAttributes().get("roomNameHash"))) {

                try {

                    // {"name":"변형근", "msg":"ㅇㅎ","date":"2024.04.26.오후 03:15:23"}
                    ChatDTO cDTO = new ChatDTO();
                    cDTO.setName("관리자");
                    cDTO.setMsg(nickname + "님이" + roomName + "채팅방에 입장하셨습니다.");
                    cDTO.setDate(DateUtil.getDateTime("yyyyMMdd hh:mm:ss"));

                    String json = new ObjectMapper().writeValueAsString(cDTO);
                    log.info("json : " + json);

                    TextMessage chatMsg = new TextMessage(json);
                    s.sendMessage(chatMsg);

                    cDTO = null;

                } catch (IOException e) {
                    log.info("Error : " + e);
                }
            }
        });

        // 기존 세션에 존재하지 않으면, 신규 세션이기에 저장함
        if (!clients.contains(session)) {

            clients.add(session); // 접속된 세션 저장

            roomInfo.put(roomName, roomNameHash); // 생성된 채팅룸 이름 저장

            log.info("session open : " + session);
        }

        log.info(this.getClass().getName() + "채팅 접속 핸들러 종료!");
    }

    /**
     * 클라이언트가 접속 해제 시 호출되는 메서드
     *
     * @param session
     * @param status
     * @throws Exception
     * */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        log.info(this.getClass().getName() + "채팅 접속 해제 핸들러 시작!");

        String roomName = CmmUtil.nvl((String) session.getAttributes().get("roomName"));
        String nickname = CmmUtil.nvl((String) session.getAttributes().get("nickname"));
        String roomNameHash = CmmUtil.nvl((String) session.getAttributes().get("roomNameHash"));

        log.info("roomName : " + roomName);
        log.info("nickname : " + nickname);
        log.info("roomNameHash : " + roomNameHash);

        clients.remove(session); // 접속되어있는 세션 삭제

        // 웹소켓에 접속된 모든 사용자 검색
        clients.forEach(s -> {

            // 내가 접속된 채팅방에 있는 세션만 메시지 보내기
            if (roomNameHash.equals(s.getAttributes().get("roomNameHash"))) {

                try {
                   // {"name":"권은아", "msg":"ㅇㅎ","date":"2024.04.26.오후 03:15:23"}
                    ChatDTO cDTO = new ChatDTO();
                    cDTO.setName("관리자");
                    cDTO.setMsg(nickname + "님이" + roomName + "채팅방에 퇴장하셨습니다.");
                    cDTO.setDate(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"));

                    String json = new ObjectMapper().writeValueAsString(cDTO);
                    log.info("json : " + json);

                    TextMessage chatMsg = new TextMessage(json);

                    cDTO = null;

                } catch (IOException e) {
                    log.info("Error : " + e);
                }
            }
        });

        log.info(this.getClass().getName() + "채팅 접속 해제 핸들러 종료!");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        log.info(this.getClass().getName() + "채팅 발송 핸들러 시작!");

        String roomName = CmmUtil.nvl((String) session.getAttributes().get("roomName"));
        String nickname = CmmUtil.nvl((String) session.getAttributes().get("nickname"));
        String roomNameHash = CmmUtil.nvl((String) session.getAttributes().get("roomNameHash"));

        log.info("roomName : " + roomName);
        log.info("nickname : " + nickname);
        log.info("roomNameHash : " + roomNameHash);

        // 채팅 메시지
        String msg = CmmUtil.nvl(message.getPayload());
        log.info("msg : " + msg);

        // 발송시간 추가를 위해 JSON 문자열을 DTO로 변환
        ChatDTO cDTO = new ObjectMapper().readValue(msg, ChatDTO.class);

        // 메시지 발송시간 서버 시간으로 설정하여 추가하기
        cDTO.setDate(DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss"));

        // ChatDTO를 JSON으로 다시 변환하기
        String json = new ObjectMapper().writeValueAsString(cDTO);

        log.info("json : " + json);

        // 웹소켓에 접속된 모든 사용자 검색
        clients.forEach(s -> {

            // 내가 접속한 채팅방에 있는 세션만 메시지 보내기
            if (roomNameHash.equals(s.getAttributes().get("roomNameHash"))) {
                try {

                    TextMessage chatMsg = new TextMessage(json);
                    s.sendMessage(chatMsg);

                } catch (IOException e) {
                    log.info("Error : " + e);
                }
            }
        });

        log.info(this.getClass().getName() + "채팅 발송 핸들러 종료!");
    }
}
