package kopo.poly.service;

import kopo.poly.dto.TestDTO;

import java.util.List;

public interface ITestService {

    // 테스트 결과 저장하기
    int insertTestResult(TestDTO pDTO) throws Exception;

    // 테스트 결과 가져오기
    TestDTO getTestResult(TestDTO pDTO) throws Exception;

    /* 자가진단 ID로 마음체크 목록 조회 */
    List<TestDTO> getTestByUserId(TestDTO pDTO) throws Exception;
}
