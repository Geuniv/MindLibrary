package kopo.poly.service;

import kopo.poly.dto.HospitalDTO;

import java.util.List;

public interface IHospitalService {

    /**
     * 병원 정보 가져오기 ( 지도 ) - ( 2024.06.08 )
     */
    List<HospitalDTO> getHospitalInfo(int page, int itemsPerPage, HospitalDTO pDTO) throws Exception;

    /**
     * 공공데이터포털 API ( XML ) 호출 후 파싱해서 MongoDB 컬렉션에 병원 정보 저장 ( 2024.06.08 )
     */
    int collectHospital() throws Exception;

    /**
     * 스케쥴링에 따라 값 삭제 ( 2024.06.10 )
     */
    int dropHospital() throws Exception;

}
