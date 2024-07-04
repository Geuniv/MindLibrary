package kopo.poly.persistance.mapper;

import kopo.poly.dto.CheckDTO;
import kopo.poly.dto.TestDTO;
import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ITestMapper {

    // 자가진단 결과 저장하기
    int insertTestResult(TestDTO pDTO) throws Exception;

    // 자가진단 결과 가져오기
    TestDTO getTestResult(TestDTO pDTO) throws Exception;

    /* 자가진단 ID로 마음체크 목록 조회 */
    List<TestDTO> getTestByUserId(TestDTO pDTO) throws Exception;
}
