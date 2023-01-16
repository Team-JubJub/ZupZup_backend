package zupzup.back_end.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zupzup.back_end.dto.StoreDto;
import zupzup.back_end.service.StoreService;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/seller")
public class StoreController {

    private final StoreService storeService;

    /**
     * 메인 페이지(관리 관련) 컨트롤러
     */

    @GetMapping("/{storeId}")
    public StoreDto managementMain(@PathVariable Long storeId) {
        // 가게 관련 내용 (가게 이름 및 운영 시간, 이벤트 내용, 오늘 할인 시간)
        // 제품 관련 내용 ([제품 이미지, 제품 이름, 가격])
        // Store 관련 DTO 전체 넘김
        return storeService.mainPage(storeId);
    }
}
