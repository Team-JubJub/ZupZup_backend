package com.rest.api.info.service;

import com.zupzup.untact.social.utils.AuthUtils;
import com.zupzup.untact.model.domain.auth.user.User;
import com.zupzup.untact.model.dto.auth.customer.UserDto;
import com.zupzup.untact.model.dto.info.customer.request.PatchNickNameDto;
import com.zupzup.untact.model.dto.info.customer.request.PatchOptionalTermDto;
import com.zupzup.untact.model.dto.info.customer.request.PatchPhoneNumberDto;
import com.zupzup.untact.model.dto.info.customer.response.GetInfoResponseDto;
import com.zupzup.untact.model.dto.info.customer.response.PatchInfoResponseDto;
import com.zupzup.untact.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class InfoService {

    @Autowired
    ModelMapper modelMapper;

    private final UserRepository userRepository;
    private final AuthUtils authUtils;

    // <-------------------- GET part -------------------->
    public GetInfoResponseDto getUserInfo(String accessToken) {
        User userEntity = authUtils.getUserEntity(accessToken); // 액세스 토큰을 이용하여 유저 정보 반환
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        GetInfoResponseDto userInfoDto = new GetInfoResponseDto(userDto, "Get user data succeed.");

        return userInfoDto;
    }

    // <-------------------- PATCH part -------------------->
    public PatchInfoResponseDto updatePhoneNumber(String accessToken, PatchPhoneNumberDto patchPhoneNumberDto) {
        User userEntity = authUtils.getUserEntity(accessToken); // 액세스 토큰을 이용하여 유저 정보 반환
        userEntity.updatePhoneNumber(patchPhoneNumberDto);    // 전화번호 변경
        userRepository.save(userEntity);
        UserDto updatedUserDto = modelMapper.map(userEntity, UserDto.class);

        PatchInfoResponseDto patchPhoneNumberResponseDto = new PatchInfoResponseDto(updatedUserDto, "Phone number updated.");

        return patchPhoneNumberResponseDto;
    }

    public PatchInfoResponseDto updateNickName(String accessToken, PatchNickNameDto patchNickNameDto) {
        User userEntity = authUtils.getUserEntity(accessToken); // 액세스 토큰을 이용하여 유저 정보 반환
        if (userRepository.findByNickName(patchNickNameDto.getNickName()).isPresent()) {    // 해당 닉네임이 존재하면
            return null;    // null 반환
        }

        userEntity.updateNickName(patchNickNameDto);    // 닉네임 변경
        userRepository.save(userEntity);
        UserDto updatedUserDto = modelMapper.map(userEntity, UserDto.class);

        PatchInfoResponseDto patchNicknameResponseDto = new PatchInfoResponseDto(updatedUserDto, "Nickname updated.");

        return patchNicknameResponseDto;
    }

    public PatchInfoResponseDto updateOptionalTerm(String accessToken, PatchOptionalTermDto patchOptionalTermDto) {
        User userEntity = authUtils.getUserEntity(accessToken); // 액세스 토큰을 이용하여 유저 정보 반환
        userEntity.updateOptionalTerm1(patchOptionalTermDto);    // 선택 약관 동의 여부 변경
        userRepository.save(userEntity);
        UserDto updatedUserDto = modelMapper.map(userEntity, UserDto.class);

        PatchInfoResponseDto patchOptionalTermResponseDto = new PatchInfoResponseDto(updatedUserDto, "Optional term's agree or not updated.");

        return patchOptionalTermResponseDto;
    }

}
