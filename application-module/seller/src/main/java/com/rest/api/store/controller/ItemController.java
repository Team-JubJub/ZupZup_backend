package com.rest.api.store.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import dto.item.ItemDto;
import dto.item.seller.request.ItemRequestDto;
import dto.item.seller.request.PostItemDto;
import dto.item.seller.request.UpdateRequestDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.rest.api.store.service.ItemService;

import java.util.List;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/seller")
public class ItemController {

    private final ItemService itemService;

    /**
     * 아이템 컨트롤러
     */
    @Tag(name = "아이템 저장", description = "새로운 아이템 저장용")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "상품 (상품명)(이)가 저장되었습니다.")
    )
    @PostMapping("/{storeId}") // 상품 저장
    public ResponseEntity saveItem(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                   @RequestPart(value = "item") PostItemDto requestDto,
                                   @RequestPart(value = "image", required = false) MultipartFile itemImg,
                                   @PathVariable Long storeId) throws Exception {

        String itemName = itemService.saveItem(requestDto, itemImg, storeId);
        String format = String.format("상품 %s(이)가 저장되었습니다.", itemName);
        return new ResponseEntity(format, HttpStatus.CREATED); // 상품의 이름 반환
    }

    @Tag(name = "아이템 업데이트", description = "기존 아이템 업데이트")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "수정된 response(확인용)")
    )
    @PatchMapping("/{storeId}/{itemId}")
    public ResponseEntity updateItem(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                     @PathVariable Long itemId,
                                     @PathVariable Long storeId,
                                     @RequestPart(value = "item") UpdateRequestDto updateDto,
                                     @RequestPart(value = "image", required = false) MultipartFile itemImg) throws Exception {


        String response = itemService.updateItem(itemId, storeId, updateDto, itemImg);

        return new ResponseEntity(response, HttpStatus.OK); //완료 여부 반환
    }

    @Tag(name = "아이템 삭제", description = "아이템 삭제")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "(아무것도 X)")
    )
    @DeleteMapping("/{storeId}/{itemId}")
    public ResponseEntity deleteItem(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                     @PathVariable Long itemId, @PathVariable String storeId) {

        String response = itemService.deleteItem(itemId);
        return new ResponseEntity(response, HttpStatus.OK); //삭제 여부 반환
    }

    @Tag(name = "아이템 읽어오기", description = "전체 아이템 읽어오기")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "전체 아이템 리스트 제공")
    )
    // 전체 제품 불러오기
    @GetMapping("/{storeId}/management")
    public ResponseEntity readItems(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                    @PathVariable Long storeId) {

        List<ItemDto.getDto> dtoList = itemService.readItems(storeId);
        return new ResponseEntity(dtoList, HttpStatus.OK);
    }

    @Tag(name = "갯수 수정", description = "아이템 갯수 수정")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "수정이 완료되었습니다.")
    )
    // 제품 개수 수정하기
    @PatchMapping("/{storeId}/quantity")
    public ResponseEntity modifyQuantity(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                         @PathVariable Long storeId,
                                         @RequestPart(name = "quantity") List<ItemRequestDto.patchDto> quantityList) {

        String result = itemService.modifyQuantity(storeId, quantityList);
        return new ResponseEntity(result, HttpStatus.OK);
    }

}