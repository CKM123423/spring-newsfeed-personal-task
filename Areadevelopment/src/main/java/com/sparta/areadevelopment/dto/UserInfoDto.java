package com.sparta.areadevelopment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

/**
 * 유저의 정보 DTO
 */
@Getter
public class UserInfoDto {

    /**
     * @String username  아이디
     * @String nickname  별명
     * @String info  한줄소개
     * @String email 이메일
     */
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[0-9])[a-z0-9]{10,20}$", message = "사용자 ID는 최소 10글자 이상, 최대 20글자 이하여야 합니다.")
    private String username;

    private String nickname;

    private String info;

    @Email
    private String email;

    /**
     * DTO의 생성자 매서드
     */
    @Builder
    public UserInfoDto(String username, String nickname, String info, String email) {
        this.username = username;
        this.nickname = nickname;
        this.info = info;
        this.email = email;
    }
}
