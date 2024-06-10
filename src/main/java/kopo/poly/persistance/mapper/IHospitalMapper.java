package kopo.poly.persistance.mapper;

import kopo.poly.dto.HospitalDTO;

import java.util.List;

public interface IHospitalMapper {

    /**
     *
     * 병원 지도
     *
     * @param colNm 조회할 컬렉션 이름
     * @return 동물병원 리스트
     * */
    List<HospitalDTO> getHospitalInfo(String colNm, int page, int itemsPerPage, HospitalDTO pDTO) throws Exception;

    /**
     * 병원 리스트 저장하기
     *
     * @param pList 저장될 정보
     * @param colNm 저장할 컬렉션 이름
     * @return 저장 결과
     */
    int insertHospital(List<HospitalDTO> pList, String colNm) throws Exception;

    /**
     * 스케쥴링에 따라 값 삭제
     */
    int deleteHospital(String colNm) throws Exception;

}
