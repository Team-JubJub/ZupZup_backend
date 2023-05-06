package com.rest.api.auth.controller;

import com.rest.api.auth.service.MobileAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/mobile")
public class MobileAuthController {

    private final MobileAuthService mobileAuthService;

    @RequestMapping("/sign-in")
    public ResponseEntity singIn() {


        return new ResponseEntity(HttpStatus.OK);
    }
}
