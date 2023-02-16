package org.rajman.authentication.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.rajman.authentication.model.dto.AuthenticationToken;
import org.rajman.authentication.model.dto.LoginDTO;
import org.rajman.authentication.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserController {

    TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationToken> login(@RequestBody LoginDTO loginDTO){
        return ResponseEntity.ok(tokenService.login(loginDTO));
    }
}
