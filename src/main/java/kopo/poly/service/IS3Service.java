package kopo.poly.service;

import kopo.poly.dto.FileDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {

    /* 파일 업로드 */
    FileDTO uploadFile(MultipartFile mf, String ext) throws Exception;

    /* URL에서 파일을 다운로드 하여 S3에 업로드하는 메서드 */
    FileDTO uploadFileFromUrl(String fileUrl, String ext) throws Exception;
}
