package com.sparta.areadevelopment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeRequestDto {

    private String oldPassword;
    private String newPassword;
}
