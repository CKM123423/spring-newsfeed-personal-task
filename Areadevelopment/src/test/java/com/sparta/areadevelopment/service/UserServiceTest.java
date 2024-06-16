package com.sparta.areadevelopment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.sparta.areadevelopment.dto.UpdateUserDto;
import com.sparta.areadevelopment.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("userService In compareUserIds Not Equals Test")
    void compareUserIds_Not_Equals_Test() {
        // Given
        Long urlUserId = 1L;
        User user = Mockito.mock(User.class);
        given(user.getId()).willReturn(2L);

        // When
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserProfile(urlUserId, user);
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("유저 정보가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("userService In checkPassword Not Equals Test")
    void checkPassword_Not_Equals_Test() {
        // Given
        Long userId = 1L;
        User user = Mockito.mock(User.class);
        given(user.getId()).willReturn(1L);
        // myPassword123 의 암호화된 비밀번호
        given(user.getPassword()).willReturn(
                "$2a$10$E6wJZPa0zrQ5V9PhXgfjHuXJG6yFZJ1Oqe9U9T6B6sFS1N5VVVYa2");
        UpdateUserDto updateUserDto = Mockito.mock(UpdateUserDto.class);
        given(updateUserDto.getPassword()).willReturn("myPassword1232");

        given(passwordEncoder.matches(updateUserDto.getPassword(), user.getPassword())).willReturn(
                false);

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateProfile(userId, updateUserDto, user.getId(), user.getPassword());
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("Invalid password.");
    }
}
