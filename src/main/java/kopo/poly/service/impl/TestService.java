package kopo.poly.service.impl;

import kopo.poly.dto.TestDTO;
import kopo.poly.persistance.mapper.ITestMapper;
import kopo.poly.service.ITestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestService implements ITestService {

    private final ITestMapper testMapper;

    // 테스트 결과 저장하기
    @Override
    public int insertTestResult(TestDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 자가진단 저장 시작 !");

        return testMapper.insertTestResult(pDTO);
    }

    // 테스트 결과 가져오기
    @Override
    public TestDTO getTestResult(TestDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 자가진단 조회 시작 !");

        return testMapper.getTestResult(pDTO);
    }

    @Override
    public List<TestDTO> getTestByUserId(TestDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 자가진단 프로필 조회 시작 !");

        return testMapper.getTestByUserId(pDTO);
    }
}
