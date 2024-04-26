package kopo.poly.service;

import kopo.poly.dto.FileDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {

    /* 파일 업로드 */
    FileDTO uploadFile(MultipartFile mf, String ext) throws Exception;
}
