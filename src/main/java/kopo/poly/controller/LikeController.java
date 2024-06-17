package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.LikeDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.service.ILikeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping(value = "/like")
@Slf4j
@Controller
@RequiredArgsConstructor
public class LikeController {

    private final ILikeService likeService;

    @ResponseBody
    @PostMapping(value = "/boardLike")
    public MsgDTO boardLike(HttpSession session, HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".boardLike Start!");

        String msg = "";
        int res = 0;

        try {

            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String bSeq = CmmUtil.nvl(request.getParameter("bSeq"));

            log.info("userId : " + userId);
            log.info("bSeq : " + bSeq);

            LikeDTO pDTO = LikeDTO.builder().userId(userId).boardSeq(bSeq).build();

            if(CmmUtil.nvl(userId).equals("")) {
                msg = "로그인 후 이용 가능합니다.";
                res = 2;
            } else {
                if(likeService.getLike(pDTO) == 1) {
                    likeService.deleteLike(pDTO);
                    msg = "좋아요를 취소히였습니다";
                    res = 1;
                } else {
                    likeService.insertLike(pDTO);
                    msg = "좋아요를 추가되었습니다";
                    res = 1;
                }
            }

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".boardLike End!");
        }

        return MsgDTO.builder().msg(msg).result(res).build();
    }

}
