package kopo.poly.service.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.mongodb.client.MongoCollection;
import kopo.poly.dto.HospitalDTO;
import kopo.poly.persistance.mapper.IHospitalMapper;
import kopo.poly.service.IHospitalService;
import kopo.poly.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class HospitalService implements IHospitalService {

    private final IHospitalMapper hospitalMapper;
    private final MongoTemplate mongoTemplate;

    @Value("${hospital.api.key}")
    private String key;

    /**
     * 병원 정보 가져오기 ( 지도 ) - ( 2024.06.08 )
     */
    @Override
    public List<HospitalDTO> getHospitalInfo(int page, int itemsPerPage, HospitalDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".service 병원 정보 가져오기 시작 !");
        String colNm = "MIND_HOSPITAL";
        List<HospitalDTO> rList = hospitalMapper.getHospitalInfo(colNm, page, itemsPerPage, pDTO);
        log.info("rList : " + rList);
        log.info(this.getClass().getName() + ".service 병원 정보 가져오기 종료 !");
        return rList;
    }

    /**
     * 공공데이터포털 API ( XML ) 호출 후 파싱해서 MongoDB 컬렉션에 병원 정보 저장 ( 2024.06.08 )
     */
    @Scheduled(cron = "* 59 11 * * 1" )
    @Override
    public int collectHospital() throws Exception {
        log.info(this.getClass().getName() + ".service 병원 정보 저장 시작 !");
        int res = 0;
        String colNm = "MIND_HOSPITAL";

        // 기존 컬렉션 삭제
        hospitalMapper.deleteHospital(colNm);

        String url = "http://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList?";
        String numOfRows = "10000";
        int pageNo = 1;
        boolean hasMoreData = true;
        List<HospitalDTO> pList = new LinkedList<>();

        // API로부터 데이터를 반복해서 가져옴
        while (hasMoreData) {
            String apiParam = "serviceKey=" + key + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
            log.info("apiParam : " + apiParam);
            String xml = NetworkUtil.get(url + apiParam);
            log.info("xml : " + xml);

            // XML 응답을 맵으로 파싱
            XmlMapper xmlMapper = new XmlMapper();
            Map<String, Object> rMap = xmlMapper.readValue(xml, LinkedHashMap.class);
            Map<String, Object> bodyMap = (Map<String, Object>) rMap.get("body");
            Object itemsObj = bodyMap.get("items");

            // items가 비어있지 않으면 데이터 처리
            if (itemsObj != null && !itemsObj.toString().isEmpty()) {
                Map<String, Object> itemsMap = (Map<String, Object>) itemsObj;
                Object itemListObj = itemsMap.get("item");
                List<Map<String, Object>> itemList;

                // item이 하나의 맵인지 리스트인지 확인하고 처리
                if (itemListObj instanceof Map) {
                    itemList = new LinkedList<>();
                    itemList.add((Map<String, Object>) itemListObj);
                } else {
                    itemList = (List<Map<String, Object>>) itemListObj;
                }

                // itemList가 비어있지 않으면 데이터 추출
                if (itemList != null && !itemList.isEmpty()) {
                    for (Map<String, Object> rowMap : itemList) {
                        // 각 필드를 문자열로 변환하여 추출
                        String addr = String.valueOf(rowMap.get("addr")); // 주소
                        String telno = String.valueOf(rowMap.get("telno")); // 전화번호
                        String hospUrl = String.valueOf(rowMap.get("hospUrl")); // 홈페이지
                        String estbDd = String.valueOf(rowMap.get("estbDd")); // 개설일자
                        String postNo = String.valueOf(rowMap.get("postNo")); //우편번호
                        String sidoCd = String.valueOf(rowMap.get("sidoCd")); //시도 코드
                        String sgguCd = String.valueOf(rowMap.get("sgguCd")); // 시군구 코드
                        String emdongNm = String.valueOf(rowMap.get("emdongNm")); // 읍면동명
                        String yadmNm = String.valueOf(rowMap.get("yadmNm")); // 병원명(UTF-8 인코딩 필요)
                        String clCd = String.valueOf(rowMap.get("clCd")); // 종별코드
                        String clCdNm = String.valueOf(rowMap.get("clCdNm")); // 종별코드명
                        String xPos = String.valueOf(rowMap.get("XPos")); // x좌표(소수점 15)
                        String yPos = String.valueOf(rowMap.get("YPos")); // y좌표(소수점 15)
                        String radius = rowMap.containsKey("radius") ? String.valueOf(rowMap.get("radius")) : null; // 단위 : 미터(m)

                        // HospitalDTO 객체 생성
                        HospitalDTO pDTO = HospitalDTO.builder()
                                .addr(addr)
                                .telno(telno)
                                .hospUrl(hospUrl)
                                .estbDd(estbDd)
                                .postNo(postNo)
                                .sidoCd(sidoCd)
                                .sgguCd(sgguCd)
                                .emdongNm(emdongNm)
                                .yadmNm(yadmNm)
                                .clCd(clCd)
                                .clCdNm(clCdNm)
                                .xPos(xPos)
                                .yPos(yPos)
                                .radius(radius)
                                .build();

                        // 리스트에 추가
                        pList.add(pDTO);
                    }
                } else {
                    // 더 이상 데이터가 없으면 종료
                    hasMoreData = false;
                }
            } else {
                // 더 이상 데이터가 없으면 종료
                hasMoreData = false;
            }

            // 페이지 번호 증가
            pageNo++;
        }

        log.info("pList : " + pList);
        log.info("데이터 총량 : " + pList.size());

        // MongoDB에 데이터 저장하기 (Bulk Insert)
        res = bulkInsertHospitalData(pList, colNm);

        // 저장된 데이터에 location 필드 업데이트하기
        updateLocationField(colNm);

        // location 필드에 2dsphere 인덱스 생성
        createLocationIndex(colNm);

        log.info(this.getClass().getName() + ".service 병원 정보 저장 종료 !");
        return res;
    }

    /**
     * MongoDB에 병원 데이터를 Bulk Insert
     * @param pList 병원 데이터 리스트
     * @param colNm 컬렉션 이름
     * @return 저장된 문서 수
     */
    private int bulkInsertHospitalData(List<HospitalDTO> pList, String colNm) {
        List<Document> documentList = new ArrayList<>();
        for (HospitalDTO hospital : pList) {
            Document doc = new Document();
            doc.append("addr", hospital.addr());
            doc.append("telno", hospital.telno());
            doc.append("hospUrl", hospital.hospUrl());
            doc.append("estbDd", hospital.estbDd());
            doc.append("postNo", hospital.postNo());
            doc.append("sidoCd", hospital.sidoCd());
            doc.append("sgguCd", hospital.sgguCd());
            doc.append("emdongNm", hospital.emdongNm());
            doc.append("yadmNm", hospital.yadmNm());
            doc.append("clCd", hospital.clCd());
            doc.append("clCdNm", hospital.clCdNm());
            doc.append("xPos", hospital.xPos());
            doc.append("yPos", hospital.yPos());
            doc.append("radius", hospital.radius());

            // 위치 필드 추가 (xPos와 yPos가 null이 아닌 경우에만)
            if (hospital.xPos() != null && !hospital.xPos().equals("null") && hospital.yPos() != null && !hospital.yPos().equals("null")) {
                List<Double> coordinates = Arrays.asList(Double.parseDouble(hospital.xPos()), Double.parseDouble(hospital.yPos()));
                doc.append("location", new Document("type", "Point").append("coordinates", coordinates));
            }

            documentList.add(doc);
        }
        mongoTemplate.getDb().getCollection(colNm).insertMany(documentList);
        return documentList.size();
    }

    /**
     * MongoDB에 저장된 병원 데이터에 location 필드를 추가
     * @param colNm 컬렉션 이름
     */
    private void updateLocationField(String colNm) {
        // 저장된 데이터에 location 필드 업데이트하기
        List<Bson> updates = new ArrayList<>();
        updates.add(new Document("$set", new Document("location",
                new Document("type", "Point")
                        .append("coordinates", Arrays.asList(
                                new Document("$convert", new Document("input", "$xPos").append("to", "double").append("onError", 0.0)),
                                new Document("$convert", new Document("input", "$yPos").append("to", "double").append("onError", 0.0))
                        ))
        )));

        mongoTemplate.getDb().getCollection(colNm).updateMany(new Document(), updates);
    }

    /**
     * MongoDB에 location 필드에 대해 2dsphere 인덱스 생성
     * @param colNm 컬렉션 이름
     */
    private void createLocationIndex(String colNm) {
        MongoCollection<Document> collection = mongoTemplate.getDb().getCollection(colNm);
        Bson index = new Document("location", "2dsphere");
        collection.createIndex(index);
    }

    /**
     * 스케쥴링에 따라 병원 정보 컬렉션 삭제
     * @return 삭제된 문서 수
     * @throws Exception
     */
    @Override
    public int dropHospital() throws Exception {
        log.info(this.getClass().getName() + ".service 병원정보 삭제 시작 !");
        String colNm = "MIND_HOSPITAL";
        int res = hospitalMapper.deleteHospital(colNm);
        log.info(this.getClass().getName() + ".service 병원정보 삭제 종료 !");
        return res;
    }
}
