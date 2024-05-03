package kopo.poly.dto;

import lombok.*;

/**
 * Please explain the class!!
 *
 * @author : lee
 * @fileName : ChatRequestMsgDto
 * @since : 1/18/24
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRequestMsgDTO {

    private String role;

    private String content;

    @Builder
    public ChatRequestMsgDTO(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
