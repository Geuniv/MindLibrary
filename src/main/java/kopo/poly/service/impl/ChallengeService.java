package kopo.poly.service.impl;

import kopo.poly.dto.BoardDTO;
import kopo.poly.dto.ChallengeDTO;
import kopo.poly.dto.ChallengeWriteDTO;
import kopo.poly.persistance.mapper.IChallengeMapper;
import kopo.poly.persistance.mapper.IChallengeWriteMapper;
import kopo.poly.service.IChallengeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChallengeService implements IChallengeService {

    private final IChallengeMapper challengeMapper;

    private final IChallengeWriteMapper challengeWriteMapper;

    /* 2024.05,.26
      * 게시글 목록 조회 코드
      */
    @Override
    public List<Map<String, Object>> getChallengeList(String userId) throws Exception {

        log.info(this.getClass().getName() + ".getChallengeList ( 서비스 ) start!");

        List<Map<String, Object>> rList = challengeMapper.getChallengeList(userId);

        log.info(this.getClass().getName() + ".getChallengeListByUserId ( 서비스 ) End!!");

        return rList;
    }


//    @Transactional
//    @Override
//    public ChallengeWriteDTO getChallengeInfo(ChallengeWriteDTO pDTO, boolean type) throws Exception {
//
//        log.info(this.getClass().getName() + ".getChallengeInfo ( 서비스 ) Start!");
//
//        // ReadCNT 업데이트 로직 제거
//
//        Map<String, Object> rMap = challengeMapper.getChallengeInfo(pDTO);
//
//        ChallengeWriteDTO rDTO = ChallengeWriteDTO.builder().challengeSeq(String.valueOf(rMap.get("challengeSeq"))
//        ).challengeWriteSeq(String.valueOf(rMap.get("challengeWriteSeq"))
//        ).userId(String.valueOf(rMap.get("userId"))
//        ).challengeWriteTitle(String.valueOf(rMap.get("challengeWriteTitle"))
//        ).challengeWriteContent(String.valueOf(rMap.get("challengeWriteContent"))
//        ).challengeWriteBooktitle(String.valueOf(rMap.get("challengeWriteBooktitle"))
//        ).build();
//
//        log.info(this.getClass().getName() + ".getChallengeInfo ( 서비스 ) End!");
//
//        return rDTO;
//    }


    @Transactional
    @Override
    public void insertChallengeInfo(ChallengeDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertChallengeInfo ( 서비스 ) Start!!");

        challengeMapper.insertChallengeInfo(pDTO);

        log.info(this.getClass().getName() + ".insertChallengeInfo ( 서비스 ) End!!");

    }

    @Transactional
    @Override
    public void deleteChallengeInfo(ChallengeDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteChallengeInfo ( 서비스 ) Start!!");

        challengeMapper.deleteChallengeInfo(pDTO);

        log.info(this.getClass().getName() + ".deleteChallengeInfo ( 서비스 ) End!!");

    }
}
