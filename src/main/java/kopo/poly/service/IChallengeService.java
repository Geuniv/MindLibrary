package kopo.poly.service;

import kopo.poly.dto.BoardDTO;
import kopo.poly.dto.ChallengeDTO;
import kopo.poly.dto.ChallengeWriteDTO;
import kopo.poly.dto.UserInfoDTO;

import java.util.List;
import java.util.Map;

public interface IChallengeService {

    /**
     * 챌린지 리스트
     * @return 조회결과
     */
    List<Map<String, Object>> getChallengeList(String userId) throws Exception;

    /**
     *  챌린지 상세 보기
     *  @param pDTO 상세내용 조회할 challengeSeq 값
     *  @param type 조회수 증가여부(수정보기는 조회수 증가하지 않음)
     *  @return 조회결과
     */
//    ChallengeWriteDTO getChallengeInfo(ChallengeWriteDTO pDTO, boolean type) throws Exception;

    /**
     * 챌린지 등록
     * @param pDTO 화면에서 입력된 커뮤니티 입력된 값들
     */
    void insertChallengeInfo(ChallengeDTO pDTO) throws Exception;

    /**
     * 커뮤니티 수정
     * @param pDTO 화면에서 입력된 수정되기 위한 커뮤니티 입력된 값들
     */
//    void updateBoardInfo(BoardDTO pDTO) throws Exception;

    /**
     * 챌린지 삭제
     * @param pDTO 삭제할 challengeSeq 값
     * */
    void deleteChallengeInfo(ChallengeDTO pDTO) throws Exception;

}
