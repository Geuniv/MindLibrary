package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.controller.response.CommonResponse;
import kopo.poly.dto.HospitalDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.service.IHospitalService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/map")
@RequiredArgsConstructor
@Controller
public class MapController {

    private final IHospitalService hospitalService;

    /**
     * 지도 기본 페이지 ( 2024.06.09 )
     */
    @GetMapping(value = "map")
    public String map() throws Exception {

        log.info(this.getClass().getName() + ".map ( 컨트롤러 ) 시작!");

        log.info(this.getClass().getName() + ".map ( 컨트롤러 ) 종료!");

        return "map/map";
    }

    /**
     * 수집된 병원 리스트 가져오기 ( 2024.06.09 )
     */
    @PostMapping(value = "/v1/getHospitalInfo")
    public ResponseEntity getHospitalInfo(HttpServletRequest request, @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "20") int itemsPerPage) throws Exception {

        log.info(this.getClass().getName() + ".controller getHospitalInfo 시작 !");

        double xPos = Double.parseDouble(CmmUtil.nvl(request.getParameter("xPos")));
        double yPos = Double.parseDouble(CmmUtil.nvl(request.getParameter("yPos")));

        log.info("xPos : " + xPos);
        log.info("yPos : " + yPos);

        HospitalDTO pDTO = HospitalDTO.builder().xPos(String.valueOf(xPos)).yPos(String.valueOf(yPos)).build();

        List<HospitalDTO> rList = Optional.ofNullable(hospitalService.getHospitalInfo(page, itemsPerPage, pDTO))
                .orElseGet(ArrayList::new);

        log.info("rList : " + rList);

        log.info(this.getClass().getName() + ".controller getHospitalInfo 종료 !");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));
    }

}
