package com.sparta.areadevelopment.service;

import com.sparta.areadevelopment.dto.SignOutRequestDto;
import com.sparta.areadevelopment.dto.SignupRequestDto;
import com.sparta.areadevelopment.dto.UpdateUserDto;
import com.sparta.areadevelopment.dto.UserInfoDto;
import com.sparta.areadevelopment.enums.StatusEnum;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long signUp(SignupRequestDto requestDto) {
        User user = new User(
                requestDto.getUsername(),
                requestDto.getNickname(),
                passwordEncoder.encode(requestDto.getPassword()),
                requestDto.getEmail(),
                requestDto.getInfo()
        );
        return userRepository.save(user).getId();
    }

    public UserInfoDto getUser(Long userId) {
        // 특정 유저 있는 지 확인
        User user = findUser(userId);

        return new UserInfoDto(user.getId(), user.getNickname(),
                user.getInfo(), user.getEmail());
    }


    @Transactional
    public void updateProfile(Long userId, UpdateUserDto requestDto) {
        // 유저 ID 있나 확인
        User user = findUser(userId);

        // password 검증 후 변경 여부 결정
        if (!passwordEncoder.matches(user.getPassword(), requestDto.getPassword())) {
            // password 변경 -> 다른 경우
            user.updatePassword(requestDto.getPassword());
        }

        // password 외 입력된 값에 대하여 변경
        user.updateInfo(requestDto);
    }


    // 이 부분은 토큰이 필요한 부분이다.
    @Transactional
    public void signOut(Long userId, SignOutRequestDto requestDto) {
        // user Id 검사 - active 인 것을 조회합니다.
        User user = findUser(userId);

        // 유효성 검사 부분 - password
        checkPassword(user.getPassword(), requestDto.getPassword());
        user.softDelete();
    }

    private User findUser(Long userId) {
        // userId를 통해서 user를 찾는다.
        return userRepository.findUserByIdAndStatus(userId, StatusEnum.ACTIVE.getStat())
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Failed to find comment with id," + userId)
                );
    }

    private void checkPassword(String encryptedPassword, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, encryptedPassword)) {
            throw new IllegalArgumentException("Invalid password.");
        }
    }
}
