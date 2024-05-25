package kopo.poly.service.impl;

import kopo.poly.dto.CheckDTO;
import kopo.poly.dto.MailDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.ICheckMapper;
import kopo.poly.service.ICheckService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CheckService implements ICheckService {

    private final ICheckMapper checkMapper;

    @Override
    public void insertCheckInfo(CheckDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertCheckInfo Start!!");

        checkMapper.insertCheckInfo(pDTO);

        log.info(this.getClass().getName() + ".insertCheckInfo End!!");

    }

    @Override
    public CheckDTO getCheckInfo(CheckDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getCheckInfo Start!");

        CheckDTO rDTO = Optional.ofNullable(checkMapper.getCheckInfo(pDTO)).orElseGet(CheckDTO::new);

        log.info(this.getClass().getName() + ".getCheckInfo End!");

        return rDTO;
    }
}
