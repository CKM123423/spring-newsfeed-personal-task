package com.sparta.areadevelopment.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.areadevelopment.dto.BoardRequestDto;
import com.sparta.areadevelopment.dto.PasswordChangeRequestDto;
import com.sparta.areadevelopment.dto.SignOutRequestDto;
import com.sparta.areadevelopment.dto.SignupRequestDto;
import com.sparta.areadevelopment.dto.UpdateUserDto;
import com.sparta.areadevelopment.dto.UserInfoDto;
import com.sparta.areadevelopment.entity.Board;
import com.sparta.areadevelopment.entity.Comment;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.enums.StatusEnum;
import com.sparta.areadevelopment.repository.BoardRepository;
import com.sparta.areadevelopment.repository.CommentRepository;
import com.sparta.areadevelopment.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CommentRepository commentRepository;

    private User testuser1;

    private Board board;
    private Comment comment;

    @BeforeAll
    void setTestUser() {
        SignupRequestDto requestDto = new SignupRequestDto(
                "TestUsername123",
                "TestNickname",
                "Abcde1234!56",
                "test1234@email.com",
                "Test User Info"
        );

        testuser1 = new User(
                requestDto.getUsername(),
                requestDto.getNickname(),
                passwordEncoder.encode(requestDto.getPassword()),
                requestDto.getEmail(),
                requestDto.getInfo()
        );

        userRepository.save(testuser1);
    }

    void createBoardAndComment() {
        BoardRequestDto requestDto = new BoardRequestDto(
                "Test Title",
                "Test Content"
        );

        board = new Board(testuser1, requestDto);

        boardRepository.save(board);

        comment = new Comment(
                "Test Comment",
                board,
                testuser1
        );
        commentRepository.save(comment);
    }


    @Test
    @Order(1)
    @DisplayName("signUp - Success Test")
    void signUp() {
        // Given
        SignupRequestDto requestDto = new SignupRequestDto(
                "TestUsername1234",
                "Test2Nickname",
                "Abcde1234!56",
                "test12344@email.com",
                "Test User Info"
        );

        // When
        Long userId = userService.signUp(requestDto);

        // Then
        User signUpUser = userRepository.findById(userId).orElse(null);

        assertThat(userId).isNotNull();
        assertThat(signUpUser).isNotNull();
        assertThat(signUpUser.getUsername()).isEqualTo(requestDto.getUsername());
        assertThat(signUpUser.getNickname()).isEqualTo(requestDto.getNickname());
        assertThat(signUpUser.getEmail()).isEqualTo(requestDto.getEmail());
        assertThat(passwordEncoder.matches(requestDto.getPassword(),
                signUpUser.getPassword())).isTrue();
        assertThat(signUpUser.getInfo()).isEqualTo(requestDto.getInfo());

    }

    @Test
    @Order(2)
    @DisplayName("getUserProfile - Success Test")
    void getUserProfile() {
        // Given
        Long userId = testuser1.getId();

        // When
        UserInfoDto infoDto = userService.getUserProfile(userId, testuser1);

        // Then
        assertThat(infoDto.getUsername()).isEqualTo(testuser1.getUsername());
        assertThat(infoDto.getNickname()).isEqualTo(testuser1.getNickname());
        assertThat(infoDto.getEmail()).isEqualTo(testuser1.getEmail());
        assertThat(infoDto.getInfo()).isEqualTo(testuser1.getInfo());
    }

    @Test
    @Order(3)
    @DisplayName("updateProfile - Success Test")
    void updateProfile() {
        // Given
        Long userId = testuser1.getId();

        UpdateUserDto updateUserDto = new UpdateUserDto(
                "update Nickname",
                "update123@emila.com",
                "Update Info",
                "Abcde1234!56"
        );

        // When
        userService.updateProfile(userId, updateUserDto, testuser1.getId(),
                testuser1.getPassword());

        // Then
        assertThat(testuser1.getNickname()).isNotEqualTo(updateUserDto.getNickname());
        assertThat(testuser1.getEmail()).isNotEqualTo(updateUserDto.getEmail());
        assertThat(testuser1.getInfo()).isNotEqualTo(updateUserDto.getInfo());

        assertThat(passwordEncoder.matches(updateUserDto.getPassword(),
                testuser1.getPassword())).isTrue();
    }

    @Test
    @Order(4)
    @DisplayName("signOut - Success Test")
    void signOut() {
        // Given
        Long userId = testuser1.getId();
        SignOutRequestDto requestDto = new SignOutRequestDto(
                "TestUsername123",
                "Abcde1234!56"
        );
        this.createBoardAndComment();

        // When
        userService.signOut(userId, requestDto, testuser1.getId(), testuser1.getPassword());

        // Then
        // 현재 저장된 testUser1 은 준영속상태여서 상태가 바뀌지않아 다시 저장된 정보를 불러옴
        testuser1 = userRepository.findById(userId).orElse(null);

        assertThat(testuser1).isNotNull();
        assertThat(testuser1.getStatus()).isEqualTo(StatusEnum.DELETED);
        assertThat(testuser1.getExpired()).isTrue();

        assertThat(testuser1.getDeletedAt()).isNotNull();
        assertThat(board.getDeletedAt()).isNotNull();
        assertThat(comment.getDeletedAt()).isNotNull();
    }

    @Test
    @Order(5)
    @DisplayName("updatePassword - Success Test")
    void updatePassword_Success_Test() {
        // Given
        Long userId = testuser1.getId();
        PasswordChangeRequestDto requestDto = new PasswordChangeRequestDto(
                "Abcde1234!56",
                "zZascs!234567"
        );

        // When
        userService.updatePassword(userId, requestDto, testuser1);

        // Then
        assertThat(passwordEncoder.matches(requestDto.getOldPassword(),
                testuser1.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(requestDto.getNewPassword(),
                testuser1.getPassword())).isTrue();

    }
}