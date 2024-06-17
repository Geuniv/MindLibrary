package kopo.poly.persistance.mapper;

import kopo.poly.dto.BoardDTO;
import kopo.poly.dto.CheckDTO;
import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ICheckMapper {

    /* 마음체크 결과 저장 */
    void insertCheckInfo(CheckDTO pDTO) throws Exception;

    /* 마음체크 결과 조회 */
    CheckDTO getCheckInfo(CheckDTO pDTO) throws Exception;

    /* 사용자 ID로 마음체크 목록 조회 */
    List<CheckDTO> getCheckByUserId(CheckDTO pDTO) throws Exception;
}
