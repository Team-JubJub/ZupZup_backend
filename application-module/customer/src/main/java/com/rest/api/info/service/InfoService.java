package com.rest.api.info.service;

import com.rest.api.auth.jwt.JwtTokenProvider;
import domain.auth.User.User;
import dto.auth.customer.UserDto;
import dto.auth.customer.request.PatchNickNameDto;
import dto.auth.customer.response.PatchNicknameResponseDto;
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

    public String updatePhoneNumber() {


        return "temp";
    }

    public PatchNicknameResponseDto updateNickName(String accessToken, PatchNickNameDto patchNickNameDto) {
        String providerUserId = jwtTokenProvider.getProviderUserId(accessToken);    // 유저의 id 조회
        User userEntity = userRepository.findByProviderUserId(providerUserId).get();
        userEntity.updateNickName(patchNickNameDto);    // 닉네임 변경
        userRepository.save(userEntity);
        UserDto updatedUserDto = modelMapper.map(userEntity, UserDto.class);

        PatchNicknameResponseDto patchNicknameResponseDto = new PatchNicknameResponseDto(updatedUserDto, "Nickname updated.");

        return patchNicknameResponseDto;
    }

}
