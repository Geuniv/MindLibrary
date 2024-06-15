package kopo.poly.persistance.mapper;

import kopo.poly.dto.ChallengeWriteDTO;
import kopo.poly.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IChallengeWriteMapper {

    /** 2024.05.27
     * 마음쓰기 리스트 
     */
    List<ChallengeWriteDTO> getChallengeWriteList(ChallengeWriteDTO pDTO) throws Exception;

    /** 2024.05.27
     * 마음쓰기 상세보기
     */
    ChallengeWriteDTO getChallengeWriteInfo(ChallengeWriteDTO pDTO);

    /** 2024.05.27
     * 마음쓰기 등록 
     */
    void insertChallengeWriteInfo(ChallengeWriteDTO pDTO) throws Exception;

    /** 2024.05.27
     * 마음쓰기 수정
     */
    void updateChallengeWriteInfo(ChallengeWriteDTO pDTO) throws Exception;

    /** 2024.05.27
     * 마음쓰기 삭제 
     */
    void deleteChallengeWriteInfo(ChallengeWriteDTO pDTO) throws Exception;
}
