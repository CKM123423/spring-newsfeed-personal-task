package com.sparta.areadevelopment.service;

import com.sparta.areadevelopment.dto.PasswordChangeRequestDto;
import com.sparta.areadevelopment.dto.SignOutRequestDto;
import com.sparta.areadevelopment.dto.SignupRequestDto;
import com.sparta.areadevelopment.dto.UpdateUserDto;
import com.sparta.areadevelopment.dto.UserInfoDto;
import com.sparta.areadevelopment.entity.Board;
import com.sparta.areadevelopment.entity.Comment;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.repository.BoardRepository;
import com.sparta.areadevelopment.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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

    public UserInfoDto getUserProfile(Long userId, User user) {
        getUserDetails(userId, user);

        return new UserInfoDto(user.getUsername(), user.getNickname(),
                user.getInfo(), user.getEmail());
    }


    @Transactional
    public void updateProfile(Long userId, UpdateUserDto requestDto, User user) {
        // customUserDetails를 이용해서, 유저를 찾고 검증 로직을 안에다 넣자
        getUserDetails(userId, user);
        checkPassword(user.getPassword(), requestDto.getPassword());
        user.updateInfo(requestDto);
    }

    public void updatePassword(Long userId, PasswordChangeRequestDto requestDto,
            User user) {

        getUserDetails(userId, user);
        checkPassword(user.getPassword(), requestDto.getOldPassword()); // 저장되어 있는 비밀번호와 맞는지 검증
        user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(user);
    }

    // 이 부분은 토큰이 필요한 부분이다.
    @Transactional
    public void signOut(Long userId, SignOutRequestDto requestDto, User user) {
        getUserDetails(userId, user);
        checkPassword(user.getPassword(), requestDto.getPassword());

        List<Board> boards = boardRepository.findByUserId(user.getId());
        boards.forEach(board -> {
            board.softDelete();
            board.getComments().forEach(Comment::delete); // 각 게시물의 댓글도 소프트 딜리트
        });
        user.softDelete();
        user.setExpired(true); // 회원 탈퇴시 true로 더 이상 다른 로직이 불가하게 만듭니다.
    }

    private void checkPassword(String encryptedPassword, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, encryptedPassword)) {
            throw new IllegalArgumentException("Invalid password.");
        }
    }

    private static void getUserDetails(Long userId, User user) {
        if (!Objects.equals(userId, user.getId())) {
            throw new UsernameNotFoundException(user.getUsername());
        }
    }
}
