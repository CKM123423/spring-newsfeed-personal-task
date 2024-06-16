package com.sparta.areadevelopment.controller;

import com.sparta.areadevelopment.dto.PasswordChangeRequestDto;
import com.sparta.areadevelopment.dto.SignOutRequestDto;
import com.sparta.areadevelopment.dto.SignupRequestDto;
import com.sparta.areadevelopment.dto.UpdateUserDto;
import com.sparta.areadevelopment.dto.UserInfoDto;
import com.sparta.areadevelopment.entity.CustomUserDetails;
import com.sparta.areadevelopment.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DTO의 생성자 매서드
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * DTO의 생성자 매서드
     */
    @Autowired
    private final UserService userService;

    /**
     * DTO의 생성자 매서드
     */
    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(
            @Valid @RequestBody SignupRequestDto requestDto) {
        userService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body("Sign up successful");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDto> getUserProfile(
            @PathVariable("userId") Long userId
            , @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserProfile(userId, userDetails.getUser()));
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateProfile(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateUserDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        userService.updateProfile(userId, requestDto, userDetails.getUser().getId(),
                userDetails.getUser().getPassword());

        return ResponseEntity.status(HttpStatus.OK).body("Profile updated successful");
    }


    // redirect status code 가 있다.
    @PutMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable("userId") Long userId,
            @RequestBody PasswordChangeRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updatePassword(userId, requestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successful");
    }


    @PostMapping("/{userId}/sign-out")
    public ResponseEntity<String> signOut(
            @PathVariable(name = "userId") Long userId,
            @RequestBody SignOutRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.signOut(userId, requestDto, userDetails.getUser().getId(),
                userDetails.getUser().getPassword());
        // token 검증
        return ResponseEntity.status(HttpStatus.OK).body("Sign out successful");
    }
}

