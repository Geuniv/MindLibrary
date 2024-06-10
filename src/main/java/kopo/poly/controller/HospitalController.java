package kopo.poly.controller;

import kopo.poly.controller.response.CommonResponse;
import kopo.poly.dto.HospitalDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.service.IHospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/hospital")
@RequiredArgsConstructor
@Controller
public class HospitalController {

    private final IHospitalService hospitalService;

    /**
     * 병원정보 리스트 저장하기
     */
    @ResponseBody
    @GetMapping(value = "collectHospital")
    public ResponseEntity collectHospital() throws Exception {

        log.info(this.getClass().getName() + ".controller 병원 정보 저장 시작!!!");

        // 수집 결과 출력
        String msg = "";

        int res = hospitalService.collectHospital();

        if (res == 1) {
            msg = "병원 정보 수집 성공!";

        } else {
            msg = "병원 정보 수집 실패!";

        }

        MsgDTO dto = MsgDTO.builder().msg(msg).result(res).build();

        log.info(this.getClass().getName() + ".controller 병원 정보 저장 종료!!!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), dto));
    }

}
