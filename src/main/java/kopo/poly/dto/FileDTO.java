package kopo.poly.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDTO {

    private String fileSeq; // 저장 순번

    private String userId; // 유저 아이디

    private Integer boardSeq; // boardSeq 필드 추가

    private String fileName; // 저장된 이미지 파일 이름

    private String filePath; // 저장된 이미지 파일의 파일 저장 경로

    private String orgFileName; // 원래 파일 이름

    private String fileSize; // 파일 크기

    private String fileUrl; // 저장된 이미지 파일 url

    private int page;               // 이미지 장수

    @Builder
    public FileDTO(String originalName, String saveName, String  fileSize) {
        this.orgFileName = originalName;
        this.fileName = saveName;
        this.fileSize = fileSize;
    }

}
