package com.rest.api.info.service;

import com.rest.api.auth.jwt.JwtTokenProvider;
import domain.auth.User.User;
import dto.auth.customer.UserDto;
import dto.info.customer.request.PatchNickNameDto;
import dto.info.customer.request.PatchOptionalTermDto;
import dto.info.customer.request.PatchPhoneNumberDto;
import dto.info.customer.response.PatchInfoResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.UserRepository;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class InfoService {

    @Autowired
    ModelMapper modelMapper;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public PatchInfoResponseDto updatePhoneNumber(String accessToken, PatchPhoneNumberDto patchPhoneNumberDto) {
        String providerUserId = jwtTokenProvider.getProviderUserId(accessToken);    // 유저의 id 조회
        User userEntity = userRepository.findByProviderUserId(providerUserId).get();
        userEntity.updatePhoneNumber(patchPhoneNumberDto);    // 전화번호 변경
        userRepository.save(userEntity);
        UserDto updatedUserDto = modelMapper.map(userEntity, UserDto.class);

        PatchInfoResponseDto patchPhoneNumberResponseDto = new PatchInfoResponseDto(updatedUserDto, "Nickname updated.");

        return patchPhoneNumberResponseDto;
    }

    public PatchInfoResponseDto updateNickName(String accessToken, PatchNickNameDto patchNickNameDto) {
        String providerUserId = jwtTokenProvider.getProviderUserId(accessToken);    // 유저의 id 조회
        User userEntity = userRepository.findByProviderUserId(providerUserId).get();
        userEntity.updateNickName(patchNickNameDto);    // 닉네임 변경
        userRepository.save(userEntity);
        UserDto updatedUserDto = modelMapper.map(userEntity, UserDto.class);

        PatchInfoResponseDto patchNicknameResponseDto = new PatchInfoResponseDto(updatedUserDto, "Nickname updated.");

        return patchNicknameResponseDto;
    }

    public PatchInfoResponseDto updateOptionalTerm(String accessToken, PatchOptionalTermDto patchOptionalTermDto) {
        String providerUserId = jwtTokenProvider.getProviderUserId(accessToken);    // 유저의 id 조회
        User userEntity = userRepository.findByProviderUserId(providerUserId).get();
        userEntity.updateOptionalTerm1(patchOptionalTermDto);    // 선택 약관 동의 여부 변경
        userRepository.save(userEntity);
        UserDto updatedUserDto = modelMapper.map(userEntity, UserDto.class);

        PatchInfoResponseDto patchOptionalTermResponseDto = new PatchInfoResponseDto(updatedUserDto, "Nickname updated.");

        return patchOptionalTermResponseDto;
    }

}
