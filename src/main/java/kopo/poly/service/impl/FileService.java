package kopo.poly.service.impl;

import kopo.poly.dto.FileDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.IFileMapper;
import kopo.poly.service.IFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService implements IFileService {

    private final IFileMapper fileMapper;

    @Override
    public List<FileDTO> getFile(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + "파일 가져오기 시작!");

        return fileMapper.getFile(pDTO);

    }

    @Override
    public void insertFile(FileDTO pDTO) throws Exception {

        log.info(this.getClass().getClass() + "insert 파일 시작!");

        fileMapper.insertFile(pDTO);

    }

    @Override
    public void updateFile(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + "파일 업데이트 시작!");

        fileMapper.updateFile(pDTO);

    }

    @Override
    public void deleteFile(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + "파일 삭제 시작!");

        fileMapper.deleteFile(pDTO);

    }
}
