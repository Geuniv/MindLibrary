package kopo.poly.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * S3Config 클래스는 Naver Cloud의 Object Storage(S3 호환 서비스)에 접근하기 위한 설정을 정의합니다.
 *
 * <p>이 클래스는 @Configuration 어노테이션을 사용하여 스프링의 설정 클래스임을 명시하고,
 * 애플리케이션이 시작될 때 AmazonS3Client 빈을 생성합니다.</p>
 *
 * <p>필드:
 * <ul>
 *     <li>naverClientId: Naver Cloud의 Access Key ID를 주입받는 필드</li>
 *     <li>naverSecretKey: Naver Cloud의 Secret Key를 주입받는 필드</li>
 *     <li>region: Naver Cloud의 S3 서비스가 제공되는 리전, 기본값은 "kr-standard"</li>
 * </ul>
 * </p>
 *
 * <p>메서드:
 * <ul>
 *     <li>amazonS3Client: AmazonS3Client 객체를 생성하고 설정된 자격 증명과 리전을 적용하여 반환하는 메서드</li>
 * </ul>
 * </p>
 *
 * <p>이 클래스는 AWS SDK for Java를 사용하여 S3 클라이언트를 생성합니다.</p>
 *
 * @see com.amazonaws.services.s3.AmazonS3Client
 * @see com.amazonaws.auth.BasicAWSCredentials
 * @see com.amazonaws.auth.AWSStaticCredentialsProvider
 * @see com.amazonaws.services.s3.AmazonS3ClientBuilder
 */
@Configuration
public class S3Config {

    // application.properties 파일에서 'naver.access_key_id' 속성 값을 주입받는 변수
    @Value("${naver.access_key_id}")
    private String naverClientId;

    // application.properties 파일에서 'naver.secret_key' 속성 값을 주입받는 변수
    @Value("${naver.secret_key}")
    private String naverSecretKey;

    // Naver Cloud의 S3 서비스가 제공되는 리전
    private String region = "kr-standard";

    /**
     * AmazonS3Client 빈을 생성합니다.
     *
     * <p>이 메서드는 BasicAWSCredentials 객체를 사용하여 Naver Cloud의 자격 증명을 설정하고,
     * AmazonS3ClientBuilder를 사용하여 S3 클라이언트를 생성합니다. 생성된 클라이언트는 Naver Cloud의
     * S3 서비스와 상호작용할 수 있습니다.</p>
     *
     * @return 설정된 AmazonS3Client 객체
     */
    @Bean
    public AmazonS3Client amazonS3Client() {
        // Naver Cloud의 자격 증명을 설정하는 객체를 생성합니다.
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(naverClientId, naverSecretKey);

        // 자격 증명과 리전을 사용하여 AmazonS3Client 객체를 생성하고 반환합니다.
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

}
