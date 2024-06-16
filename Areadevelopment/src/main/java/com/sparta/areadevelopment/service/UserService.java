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
import com.sparta.areadevelopment.repository.CommentRepository;
import com.sparta.areadevelopment.repository.UserRepository;
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
    private final CommentRepository commentRepository;
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
        compareUserIds(userId, user.getId());

        return new UserInfoDto(user.getUsername(), user.getNickname(),
                user.getInfo(), user.getEmail());
    }

    @Transactional
    public void updateProfile(Long userId, UpdateUserDto requestDto, Long tokenUserId,
            String encodingPassword) {
        // customUserDetails를 이용해서, 유저를 찾고 검증 로직을 안에다 넣자
        compareUserIds(userId, tokenUserId);
        checkPassword(requestDto.getPassword(), encodingPassword);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));
        user.updateInfo(requestDto);
    }

    public void updatePassword(Long userId, PasswordChangeRequestDto requestDto,
            User user) {
        compareUserIds(userId, user.getId());
        checkPassword(requestDto.getOldPassword(), user.getPassword()); // 저장되어 있는 비밀번호와 맞는지 검증
        user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(user);
    }

    // 이 부분은 토큰이 필요한 부분이다.
    @Transactional
    public void signOut(Long userId, SignOutRequestDto requestDto, Long tokenUserId,
            String encodingPassword) {
        compareUserIds(userId, tokenUserId);
        checkPassword(requestDto.getPassword(), encodingPassword);

        List<Board> boards = boardRepository.findByUserIdAndDeletedAtIsNull(userId);

        for (Board board : boards) {
            board.softDelete();
        }

        List<Comment> comments = commentRepository.findByUserIdAndDeletedAtIsNull(userId);

        for (Comment comment : comments) {
            comment.softDelete();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저 정보가 없습니다."));

        user.softDelete();
        user.setExpired(true); // 회원 탈퇴시 로그아웃처리도 동시에 처리
    }

    private void checkPassword(String rawPassword, String encryptedPassword) {
        if (!passwordEncoder.matches(rawPassword, encryptedPassword)) {
            throw new IllegalArgumentException("Invalid password.");
        }
    }

    private void compareUserIds(Long urlUserId, Long tokenUserId) {
        if (!Objects.equals(urlUserId, tokenUserId)) {
            throw new UsernameNotFoundException("유저 정보가 일치하지 않습니다.");
        }
    }
}
