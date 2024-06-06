package kopo.poly.persistance.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import kopo.poly.dto.MongoDTO;
import kopo.poly.persistance.mapper.AbstractMongoDBComon;
import kopo.poly.persistance.mapper.IMongoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class MongoMapper extends AbstractMongoDBComon implements IMongoMapper {

    private final MongoTemplate mongodb;

    @Override
    public int insertData(MongoDTO pDTO, String colNm) throws Exception {

        log.info(this.getClass().getName() + ".mapper 데이터 넣기 시작!");

        int res = 0;

        // 데이터를 저장할 컬렉션 생성
        super.createCollection(mongodb, colNm);

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // DTO를 Map 데이터타입으로 변경 한 뒤, 변경된 Map 데이터타입을 Document로 변경하기
        col.insertOne(new Document(new ObjectMapper().convertValue(pDTO, Map.class)));

        res = 1;

        log.info(this.getClass().getName() + ".mapper 데이터 넣기 종료!");

        return res;

    }
}
