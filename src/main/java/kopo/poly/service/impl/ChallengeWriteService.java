package kopo.poly.service.impl;

import kopo.poly.dto.ChallengeDTO;
import kopo.poly.dto.ChallengeWriteDTO;
import kopo.poly.dto.CommentDTO;
import kopo.poly.persistance.mapper.IChallengeWriteMapper;
import kopo.poly.service.IChallengeService;
import kopo.poly.service.IChallengeWriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChallengeWriteService implements IChallengeWriteService {

    private final IChallengeWriteMapper challengeWriteMapper;

    /** 2024.05.27
     * 마음쓰기 리스트 
     */
    @Override
    public List<ChallengeWriteDTO> getChallengeWriteList(ChallengeWriteDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + "getChallengeWriteList ( 서비스 ) Start!!");

        return challengeWriteMapper.getChallengeWriteList(pDTO);
    }

    @Override
    public ChallengeWriteDTO getChallengeWriteInfo(ChallengeWriteDTO pDTO, boolean type) throws Exception {

        log.info(this.getClass().getName() + "getChallengeWriteInfo ( 서비스 ) Start!!");

        return challengeWriteMapper.getChallengeWriteInfo(pDTO);
    }

    /** 2024.05.27
     * 마음쓰기 등록 
     */
    @Transactional
    @Override
    public void insertChallengeWriteInfo(ChallengeWriteDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertChallengeWriteInfo ( 서비스 ) Start!!");

        challengeWriteMapper.insertChallengeWriteInfo(pDTO);
    }

    @Override
    public void updateChallengeWriteInfo(ChallengeWriteDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateChallengeWriteInfo ( 서비스 ) Start!!");

        challengeWriteMapper.updateChallengeWriteInfo(pDTO);
    }

    /** 2024.05.27
     * 마음쓰기 삭제 
     */
    @Transactional
    @Override
    public void deleteChallengeWriteInfo(ChallengeWriteDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteChallengeWriteInfo ( 서비스 ) Start!!");

        challengeWriteMapper.deleteChallengeWriteInfo(pDTO);
    }
}
