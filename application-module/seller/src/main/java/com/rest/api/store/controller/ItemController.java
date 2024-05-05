package com.rest.api.store.controller;

import com.rest.api.store.service.ItemService;
import com.zupzup.untact.custom.jwt.CustomJwtTokenProvider;
import com.zupzup.untact.model.dto.item.seller.request.PatchItemCountDto;
import com.zupzup.untact.model.dto.item.seller.request.PostItemDto;
import com.zupzup.untact.model.dto.item.seller.request.UpdateRequestDto;
import com.zupzup.untact.model.dto.item.seller.response.GetDto;
import com.zupzup.untact.model.dto.item.seller.response.ItemResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Item", description = "상품과 관련된 API")
@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/seller")
public class ItemController {

    private final ItemService itemService;

    /**
     * 아이템 컨트롤러
     */
    @Operation(summary = "아이템 저장", description = "새로운 아이템 저장")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 (상품명)(이)가 저장되었습니다."),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료, 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n or \n" +
                            "Sign-outed or deleted user."))),
            @ApiResponse(responseCode = "404", description = "해당 가게가 존재하지 않음",
                    content = @Content(schema = @Schema(example = "{\n\t\"message\": \"해당 가게를 찾을 수 없습니다.\"\n}")))
    })
    @PostMapping("/{storeId}") // 상품 저장
    public ResponseEntity saveItem(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(CustomJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                   @RequestPart(value = "item") PostItemDto requestDto,
                                   @RequestPart(value = "image", required = false) MultipartFile itemImg,
                                   @PathVariable Long storeId) throws Exception {
        ItemResponseDto rs = itemService.saveItem(requestDto, itemImg, storeId);

        return new ResponseEntity(rs, HttpStatus.CREATED); // 상품 관련 response 제공
    }

    @Operation(summary = "전체 아이템 읽어오기", description = "전체 아이템 읽어오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 아이템 리스트 제공"),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료, 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n or \n" +
                            "Sign-outed or deleted user."))),
            @ApiResponse(responseCode = "404", description = "해당 가게가 존재하지 않음",
                    content = @Content(schema = @Schema(example = "{\n\t\"message\": \"해당 가게를 찾을 수 없습니다.\"\n}")))
    })
    // 전체 제품 불러오기
    @GetMapping("/{storeId}/management")
    public ResponseEntity readItems(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(CustomJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                    @PathVariable Long storeId) {
        List<GetDto> dtoList = itemService.readItems(storeId);

        return new ResponseEntity(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "아이템 업데이트", description = "기존 아이템 업데이트")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정된 response(확인용)"),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료, 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n or \n" +
                            "Sign-outed or deleted user."))),
            @ApiResponse(responseCode = "404", description = "해당 가게나 상품이 존재하지 않음",
                    content = @Content(schema = @Schema(example = "{\n\t\"message\": \"해당 가게(or 상품)를 찾을 수 없습니다.\"\n}")))
    })
    @PatchMapping("/{storeId}/{itemId}")
    public ResponseEntity updateItem(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(CustomJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                     @PathVariable Long itemId,
                                     @PathVariable Long storeId,
                                     @RequestPart(value = "item") UpdateRequestDto updateDto,
                                     @RequestPart(value = "image", required = false) MultipartFile itemImg) throws Exception {
        ItemResponseDto response = itemService.updateItem(itemId, storeId, updateDto, itemImg);

        return new ResponseEntity(response, HttpStatus.OK); //완료 여부 반환
    }

    @Operation(summary = "아이템 갯수 수정", description = "아이템 갯수 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정이 완료되었습니다."),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료, 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n or \n" +
                            "Sign-outed or deleted user."))),
            @ApiResponse(responseCode = "404", description = "수정하려는 상품 중 존재하지 않는 상품이 있는 경우",
                    content = @Content(schema = @Schema(example = "{\n\t\"message\": \"해당 상품을 찾을 수 없습니다.\"\n}")))
    })
    // 제품 개수 수정하기
    @PatchMapping("/{storeId}/quantity")
    public ResponseEntity modifyQuantity(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(CustomJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                         @PathVariable Long storeId,
                                         @RequestPart(name = "quantity") List<PatchItemCountDto> quantityList) {
        String result = itemService.modifyQuantity(storeId, quantityList);

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @Operation(summary = "아이템 삭제", description = "아이템 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "(아무것도 X)"),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료, 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n or \n" +
                            "Sign-outed or deleted user."))),
            @ApiResponse(responseCode = "404", description = "해당 상품이 존재하지 않는 경우",
                    content = @Content(schema = @Schema(example = "{\n\t\"message\": \"해당 상품을 찾을 수 없습니다.\"\n}")))
    })
    @DeleteMapping("/{storeId}/{itemId}")
    public ResponseEntity deleteItem(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(CustomJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                     @PathVariable Long itemId, @PathVariable String storeId) {
        String response = itemService.deleteItem(itemId);

        return new ResponseEntity(response, HttpStatus.OK); //삭제 여부 반환
    }

}