package kopo.poly.service;

import kopo.poly.dto.MongoDTO;

public interface IMongoService {

    /**
     * 간단한 데이터 저장하기 ( 2024.06.08 )
     */
    int mongoTest(MongoDTO pDTO) throws Exception;

}
