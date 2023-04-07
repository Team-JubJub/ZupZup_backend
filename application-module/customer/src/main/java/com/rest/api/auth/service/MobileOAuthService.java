package com.rest.api.auth.service;

import dto.auth.customer.request.UserRequestDto;
import org.springframework.stereotype.Service;
import repository.UserRepository;

@Service
public class MobileOAuthService {  // For not a case of OAuth2

    private UserRepository userRepository;

    // <-------------------- Sign-up part -------------------->

    // <-------------------- Sign-in part -------------------->
    public String naverOAuthLogin(String access_token, String refresh_token, UserRequestDto.UserOAuthSignInDto userOAuthSignInDto) {


        return "temp";
    }

    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    // <--- Methods for readability --->
}
