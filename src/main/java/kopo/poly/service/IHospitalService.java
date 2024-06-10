package kopo.poly.service;

import kopo.poly.dto.HospitalDTO;

import java.util.List;

public interface IHospitalService {

    /** 수집된 병원 리스트 가져오기*/
    List<HospitalDTO> getHospitalInfo(int page, int itemsPerPage, HospitalDTO pDTO) throws Exception;

    /**
     * 유기동물 리스트 저장하기
     * */
    int collectHospital() throws Exception;

    /**
     * 스케줄링에 따른 컬렉션 삭제
     */
    int dropHospital() throws Exception;

}
