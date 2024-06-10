package kopo.poly.persistance.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import kopo.poly.dto.HospitalDTO;
import kopo.poly.persistance.mapper.AbstractMongoDBComon;
import kopo.poly.persistance.mapper.IHospitalMapper;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalMapper extends AbstractMongoDBComon implements IHospitalMapper {

    // MongoDB와의 상호작용을 위한 MongoTemplate 객체
    private final MongoTemplate mongodb;

    // 병원 정보 가져오기
    @Override
    public List<HospitalDTO> getHospitalInfo(String colNm, int page, int itemsPerPage, HospitalDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".mapper 병원지도 정보 가져오기 시작 !");

        // 결과 리스트 초기화
        List<HospitalDTO> rList = new LinkedList<>();
        // MongoDB 컬렉션 객체 가져오기
        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // 페이지네이션을 위한 skip과 limit 설정
        int skip = (page - 1) * itemsPerPage;
        int limit = itemsPerPage;

        // 프로젝션 설정 - 반환할 필드 설정
        Document projection = new Document();
        projection.append("addr", "$addr"); // 주소
        projection.append("telno", "$telno"); // 전화번호
        projection.append("hospUrl", "$hospUrl"); // 홈페이지
        projection.append("estbDd", "$estbDd"); // 개설일자
        projection.append("postNo", "$postNo"); // 우편번호
        projection.append("sidoCd", "$sidoCd"); // 시도 코드
        projection.append("sgguCd", "$sgguCd"); // 시군구 코드
        projection.append("emdongNm", "$emdongNm"); // 읍면동명
        projection.append("yadmNm", "$yadmNm"); // 병원명(UTF-8 인코딩 필요)
        projection.append("zipCd", "$zipCd"); // 분류코드
        projection.append("clCd", "$clCd"); // 종별코드
        projection.append("clCdNm", "$clCdNm"); // 종별코드명
        projection.append("dgsbjtCd", "$dgsbjtCd"); // 진료과목코드
        projection.append("xPos", "$xPos"); // x좌표(소수점 15)
        projection.append("yPos", "$yPos"); // y좌표(소수점 15)
        projection.append("radius", "$radius"); // 단위 : 미터(m)
        projection.append("_id", 0); // _id 필드는 제외

        // 위치 정보를 Double 타입으로 변환
        Double fLa = Optional.ofNullable(pDTO.xPos())
                .map(Double::parseDouble)
                .orElse(null);

        Double fLo = Optional.ofNullable(pDTO.yPos())
                .map(Double::parseDouble)
                .orElse(null);

        // 검색 범위 설정 (기본값 3000.0 미터) 3km
        Double range = Optional.ofNullable(pDTO.range()).orElse(3000.0);

        log.info("fLa : " + fLa);
        log.info("fLo : " + fLo);
        log.info("range : " + range);

        // 위치 정보가 존재할 경우
        if (fLa != null && fLo != null) {

            // 기준 위치 설정
            Point refPoint = new Point(new Position(fLa, fLo));
            // 위치 기반 필터 설정
            Bson geoFilter = Filters.near("location", refPoint, range, 0.0);

            // 종별코드 필터 설정 (OR 조건)
            Bson clCdFilter = Filters.or(
                    Filters.eq("clCd", "01"), // 상급종합병원
                    Filters.eq("clCd", "11"), // 종합병원
                    Filters.eq("clCd", "29") // 정신병원
            );

            // 위치 필터와 종별코드 필터를 결합 (AND 조건)
            Bson combinedFilter = Filters.and(geoFilter, clCdFilter);

            log.info("combinedFilter : " + combinedFilter);

            // 필터와 프로젝션을 적용하여 MongoDB 쿼리 실행
            FindIterable<Document> rs = col.find(combinedFilter).projection(projection).skip(skip).limit(limit);

            // 결과 문서들을 HospitalDTO 리스트로 변환
            for (Document doc : rs) {
                String addr = CmmUtil.nvl(doc.getString("addr"));
                String telno = CmmUtil.nvl(doc.getString("telno"));
                String hospUrl = CmmUtil.nvl(doc.getString("hospUrl"));
                String estbDd = CmmUtil.nvl(doc.getString("estbDd"));
                String postNo = CmmUtil.nvl(doc.getString("postNo"));
                String sidoCd = CmmUtil.nvl(doc.getString("sidoCd"));
                String sgguCd = CmmUtil.nvl(doc.getString("sgguCd"));
                String emdongNm = CmmUtil.nvl(doc.getString("emdongNm"));
                String yadmNm = CmmUtil.nvl(doc.getString("yadmNm"));
                String zipCd = CmmUtil.nvl(doc.getString("zipCd"));
                String clCd = CmmUtil.nvl(doc.getString("clCd"));
                String clCdNm = CmmUtil.nvl(doc.getString("clCdNm"));
                String dgsbjtCd = CmmUtil.nvl(doc.getString("dgsbjtCd"));
                String xPos = CmmUtil.nvl(doc.getString("xPos"));
                String yPos = CmmUtil.nvl(doc.getString("yPos"));
                String radius = CmmUtil.nvl(doc.getString("radius"));

                HospitalDTO rDTO = HospitalDTO.builder()
                        .addr(addr)
                        .telno(telno)
                        .hospUrl(hospUrl)
                        .estbDd(estbDd)
                        .postNo(postNo)
                        .sidoCd(sidoCd)
                        .sgguCd(sgguCd)
                        .emdongNm(emdongNm)
                        .yadmNm(yadmNm)
                        .zipCd(zipCd)
                        .clCd(clCd)
                        .clCdNm(clCdNm)
                        .dgsbjtCd(dgsbjtCd)
                        .xPos(xPos)
                        .yPos(yPos)
                        .radius(radius)
                        .build();

                // 결과 리스트에 추가
                rList.add(rDTO);
            }
        }

        log.info("매퍼에서는 rList가 들어올까요?  : " + rList);
        log.info(this.getClass().getName() + ".mapper 병원지도 정보 가져오기 종료 !");

        return rList;
    }

    // 병원 정보 삽입
    @Override
    public int insertHospital(List<HospitalDTO> pList, String colNm) throws Exception {
        log.info(this.getClass().getName() + ".mapper 병원 정보 API 저장 시작 !");

        int res = 0;

        // 입력 리스트가 null일 경우 초기화
        if (pList == null) {
            pList = new LinkedList<>();
        }

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // 리스트의 각 병원 정보 문서를 컬렉션에 삽입
        for (HospitalDTO pDTO : pList) {
            col.insertOne(new Document(new ObjectMapper().convertValue(pDTO, Map.class)));
        }

        res = 1;

        log.info(this.getClass().getName() + ".mapper 병원 정보 API 저장 종료 !");

        return res;
    }

    // 컬렉션 삭제 (스케줄링에 따른)
    @Override
    public int deleteHospital(String colNm) throws Exception {
        log.info(this.getClass().getName() + ".mapper 병원 정보 삭제 시작 !");

        int res = 0;

        // MongoDB 컬렉션에서 모든 데이터 삭제
        super.dropCollection(mongodb, colNm);

        res = 1;

        log.info(this.getClass().getName() + ".mapper 병원 정보 삭제 종료 !");

        return res;
    }
}
