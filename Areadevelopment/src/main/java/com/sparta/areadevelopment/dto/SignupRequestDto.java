package com.sparta.areadevelopment.dto;

import com.sparta.areadevelopment.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원가입 정보 보내는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    /**
     * @String username 유저이름
     */
    @NotBlank(message = "Required Username")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[0-9])[a-z0-9]{10,20}$", message = "사용자 ID는 최소 10글자 이상, 최대 20글자 이하여야 합니다.")
    private String username;

    /**
     * @String nickname 별명
     */
    @NotBlank(message = "Required Nickname")
    private String nickname;

    /**
     * @String password 비밀번호
     */
    @NotBlank(message = "Required Password")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()]).{10,}$", message = "대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함합니다. \n비밀번호는 최소 10글자 이상이어야 합니다.")
    private String password;

    /**
     * @String Email 이메일
     */
    @Email
    private String email;

    /**
     * @String info 한줄 소개
     */
    private String info;
}

