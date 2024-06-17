package kopo.poly.service.impl;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import kopo.poly.dto.FileDTO;
import kopo.poly.service.IS3Service;
import kopo.poly.util.DateUtil;
import kopo.poly.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service implements IS3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${naver.access_key_id}")
    private String naverClientId;

    @Value("${naver.secret_key}")
    private String naverSecretKey;

    // Object Storage 접근 public IP
    final String endPoint = "https://kr.object.ncloudstorage.com";

    final String bucketName = "mindlibray";

    @Override
    public FileDTO uploadFile(MultipartFile mf, String ext) throws Exception {

        log.info(this.getClass().getName() + ".uploadFile 서비스 시작!");

        String uploadFilePath = FileUtil.mkdirForData();
        String uploadFileName = DateUtil.getDateTime("yyyyMMddHHmmssSSS") + "_" + mf.getOriginalFilename(); // 고유한 파일 이름 생성
        String uploadFileUrl = "";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(mf.getSize());
        objectMetadata.setContentType(mf.getContentType());

        try (InputStream inputStream = mf.getInputStream()) {

            String keyName = uploadFilePath + uploadFileName; // ex) 구분/년/월/일/파일.확장자
            uploadFileUrl = endPoint + "/" + bucketName + keyName;

            log.info("uploadFileName : " + uploadFileName);
            log.info("uploadFilePath : " + uploadFilePath);
            log.info("keyName : " + keyName);

            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(naverClientId, naverSecretKey);
            AmazonS3Client amazonS3Client = new AmazonS3Client(awsCredentials);
            amazonS3Client.setEndpoint(endPoint);

            // S3에 폴더 및 파일 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata).
                            withCannedAcl(CannedAccessControlList.PublicRead));  // 공개권한 부여

            // S3에 업로드한 폴더 및 파일 URL
            uploadFileUrl = amazonS3Client.getUrl(bucketName, keyName).toString();

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Filed upload failed", e);
        }

        return FileDTO.builder()
                .fileUrl(uploadFileUrl)
                .fileName(uploadFileName)
                .build();
    }
}
