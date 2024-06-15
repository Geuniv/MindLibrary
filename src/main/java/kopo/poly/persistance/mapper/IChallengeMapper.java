package kopo.poly.persistance.mapper;

import kopo.poly.dto.BoardDTO;
import kopo.poly.dto.ChallengeDTO;
import kopo.poly.dto.ChallengeWriteDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IChallengeMapper {

    // 챌린지 리스트
    List<Map<String, Object>> getChallengeList(String userId) throws Exception; // 챌린지 목록 조회

    // 챌린지 생성
    void insertChallengeInfo(ChallengeDTO pDTO) throws Exception;

    // 챌린지 상세보기
//    Map<String, Object> getChallengeInfo(ChallengeWriteDTO pDTO) throws Exception; // 챌린지 정보 조회

    // 챌린지 수정
//    void updateBoardInfo(BoardDTO pDTO) throws Exception;

    // 챌린지 삭제
    void deleteChallengeInfo(ChallengeDTO pDTO) throws Exception;
}
