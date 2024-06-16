package kopo.poly.persistance.mapper;

import kopo.poly.dto.HospitalDTO;

import java.util.List;

public interface IHospitalMapper {

    /**
     * 병원 정보 가져오기 ( 지도 ) - ( 2024.06.08 )
     *
     * @param colNm 조회할 컬렉션 이름
     * @return 동물병원 리스트
     */
    List<HospitalDTO> getHospitalInfo(String colNm, int page, int itemsPerPage, HospitalDTO pDTO) throws Exception;

    /**
     * 공공데이터포털 API ( XML ) 호출 후 파싱해서 MongoDB 컬렉션에 병원 정보 저장 ( 2024.06.08 )
     *
     * @param pList 저장될 정보
     * @param colNm 저장할 컬렉션 이름
     * @return 저장 결과
     */
    int insertHospital(List<HospitalDTO> pList, String colNm) throws Exception;

    /**
     * 스케쥴링에 따라 값 삭제 ( 2024.06.10 )
     */
    int deleteHospital(String colNm) throws Exception;

}
