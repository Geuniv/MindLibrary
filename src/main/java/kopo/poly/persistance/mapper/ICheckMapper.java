package kopo.poly.persistance.mapper;

import kopo.poly.dto.BoardDTO;
import kopo.poly.dto.CheckDTO;
import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ICheckMapper {

    // 감정체크 결과 저장
    void insertCheckInfo(CheckDTO pDTO) throws Exception;

    CheckDTO getCheckInfo(CheckDTO pDTO) throws Exception;
}
