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

import java.io.ByteArrayInputStream;
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

    /* 원본 코드 ( 2024.06.08 )*/
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

    /**
     * ★ 추가된 코드 ☆
     * ★ 네이버 로그인 API 프로필 사진 가져와서 저장하는 로직 추가 ( 2024.10.07 ) ☆
     * ★ URL에서 파일을 다운로드하여 S3에 업로드하는 메서드 ☆
     * @param fileUrl URL로부터 파일을 가져올 이미지 경로
     * @param ext 파일 확장자 (예: "jpg", "png")
     * @return FileDTO 업로드된 파일 정보
     * @throws Exception 예외 처리
     */
    @Override
    public FileDTO uploadFileFromUrl(String fileUrl, String ext) throws Exception {
        log.info("uploadFileFromUrl 호출 - 파일 URL: " + fileUrl + ", 확장자: " + ext);

        if (ext == null || ext.isEmpty()) {
            throw new IllegalArgumentException("파일 확장자가 올바르지 않습니다.");
        }

        // 업로드할 파일 경로 생성 (ex: 2024/10/07/)
        String uploadFilePath = DateUtil.getDateTime("yyyy/MM/dd") + "/";
        String uploadFileName = DateUtil.getDateTime("yyyyMMddHHmmssSSS") + "." + ext; // 고유한 파일 이름 생성

        // 파일 URL에서 원본 파일 이름 추출. 파일 이름이 없을 경우 기본 이름 설정.
        String originalFileName = (fileUrl != null && !fileUrl.isEmpty())
                ? fileUrl.substring(fileUrl.lastIndexOf("/") + 1)
                : "unknown_filename." + ext;

        log.info("생성된 파일 이름: " + uploadFileName);
        log.info("원본 파일 이름: " + originalFileName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        long fileSize = 0;

        try (InputStream inputStream = new java.net.URL(fileUrl).openStream()) { // URL로부터 InputStream 생성
            // InputStream에서 모든 데이터를 읽어 바이트 배열로 변환
            byte[] imageBytes = inputStream.readAllBytes();
            int dataLength = imageBytes.length;
            fileSize = (long) dataLength; // 파일 크기 설정

            log.info("읽어온 데이터 길이: " + dataLength);

            // 데이터 길이가 0이면 예외 발생
            if (dataLength <= 0) {
                throw new IOException("빈 파일입니다. 업로드할 수 없습니다.");
            }

            objectMetadata.setContentLength(dataLength); // 읽은 데이터 길이로 Content-Length 설정
            objectMetadata.setContentType("image/" + ext); // 이미지 확장자로 Content Type 설정

            // 바이트 배열을 다시 InputStream으로 변환
            InputStream uploadStream = new ByteArrayInputStream(imageBytes);

            String keyName = uploadFilePath + uploadFileName; // ex) 구분/년/월/일/파일.확장자
            log.info("S3 업로드 경로: " + keyName);

            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(naverClientId, naverSecretKey);
            AmazonS3Client amazonS3Client = new AmazonS3Client(awsCredentials);
            amazonS3Client.setEndpoint(endPoint);

            // S3에 폴더 및 파일 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName, keyName, uploadStream, objectMetadata).
                            withCannedAcl(CannedAccessControlList.PublicRead));  // 공개 권한 부여

            // S3에 업로드한 폴더 및 파일 URL
            String uploadFileUrl = amazonS3Client.getUrl(bucketName, keyName).toString();
            log.info("업로드된 파일 URL: " + uploadFileUrl);

            // FileDTO 생성 시 모든 필드를 확인하여 null 값을 빈 문자열 또는 기본값으로 설정
            return FileDTO.builder()
                    .fileUrl((uploadFileUrl != null) ? uploadFileUrl : "")     // 업로드된 파일 URL, 없을 경우 빈 문자열
                    .fileName((uploadFileName != null) ? uploadFileName : "unknown_file")   // 업로드된 파일 이름, 없을 경우 기본 이름
                    .filePath((uploadFilePath != null) ? uploadFilePath : "")   // 업로드된 파일 경로 설정, 없을 경우 빈 문자열
                    .orgFileName((originalFileName != null) ? originalFileName : "unknown_filename") // 원본 파일 이름, 없을 경우 기본 이름
                    .fileSize(String.valueOf((fileSize)))  // 파일 사이즈, String 타입으로 변환하여 설정
                    .build();

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new Exception("파일 업로드 실패: " + e.getMessage());
        }
    }
}
