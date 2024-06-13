package com.sparta.areadevelopment.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sparta.areadevelopment.dto.UpdateUserDto;
import com.sparta.areadevelopment.enums.AuthEnum;
import com.sparta.areadevelopment.enums.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 단순 엔티티 단위 테스트이므로 JPA 와 실제로 연동되는 과정과 id 값은 서비스에서 테스트.
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                "testUser",
                "testNickname",
                "Abcde12345!",
                "testuser123@email.com",
                "test User Info"
        );
    }

    @Test
    @DisplayName("User 생성 테스트")
    void test1() {
        // Given
        // setUp() 에서 진행됨

        // When
        // 객체가 생성되면서 진행됨

        // Then"testUser"
        assertThat(user.getUsername()).isEqualTo("testUser");
        assertThat(user.getNickname()).isEqualTo("testNickname");
        assertThat(user.getPassword()).isEqualTo("Abcde12345!");
        assertThat(user.getEmail()).isEqualTo("testuser123@email.com");
        assertThat(user.getInfo()).isEqualTo("test User Info");
        assertThat(user.getStatus()).isEqualTo(StatusEnum.ACTIVE);
        assertNull(user.getRefreshToken());
        assertFalse(user.getExpired());
    }

    // updateInfo 메서드 자체가 인자로 받는게 Dto 여서 정확한 테스트를 위해 Dto 를 생성후 사용
    // UpdateUserDto 에 Setter 와 생성자가 아예 존재 하지않아서 @Builder 적용 후 테스트
    @Test
    @DisplayName("updateInfo - 전체 변경")
    void test2() {
        // Given
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .nickname("updateNickname")
                .email("uadateUser@email.com")
                .info("update User Info")
                .build();

        // When
        user.updateInfo(updateUserDto);

        // Then
        assertThat(user.getNickname()).isEqualTo("updateNickname");
        assertThat(user.getEmail()).isEqualTo("uadateUser@email.com");
        assertThat(user.getInfo()).isEqualTo("update User Info");
    }

    @Test
    @DisplayName("updateInfo - 닉네임만 변경 테스트")
    void test3() {
        // Given
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .nickname("updateNickname")
                .build();

        // When
        user.updateInfo(updateUserDto);

        // Then
        assertThat(user.getNickname()).isEqualTo("updateNickname");
        assertThat(user.getEmail()).isEqualTo("testuser123@email.com");
        assertThat(user.getInfo()).isEqualTo("test User Info");
    }

    @Test
    @DisplayName("updateInfo - 이메일만 변경 테스트")
    void test4() {
        // Given
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .email("uadateUser@email.com")
                .build();

        // When
        user.updateInfo(updateUserDto);

        // Then
        assertThat(user.getNickname()).isEqualTo("testNickname");
        assertThat(user.getEmail()).isEqualTo("uadateUser@email.com");
        assertThat(user.getInfo()).isEqualTo("test User Info");
    }

    @Test
    @DisplayName("updateInfo - 한줄소개만 변경 테스트")
    void test5() {
        // Given
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .info("update User Info")
                .build();

        // When
        user.updateInfo(updateUserDto);

        // Then
        assertThat(user.getNickname()).isEqualTo("testNickname");
        assertThat(user.getEmail()).isEqualTo("testuser123@email.com");
        assertThat(user.getInfo()).isEqualTo("update User Info");
    }

    @Test
    @DisplayName("updateInfo - 닉네임, 이메일만 변경 테스트")
    void test6() {
        // Given
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .nickname("updateNickname")
                .email("uadateUser@email.com")
                .build();

        // When
        user.updateInfo(updateUserDto);

        // Then
        assertThat(user.getNickname()).isEqualTo("updateNickname");
        assertThat(user.getEmail()).isEqualTo("uadateUser@email.com");
        assertThat(user.getInfo()).isEqualTo("test User Info");
    }

    @Test
    @DisplayName("updateInfo - 닉네임, 한줄소개만 변경 테스트")
    void test7() {
        // Given
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .nickname("updateNickname")
                .info("update User Info")
                .build();

        // When
        user.updateInfo(updateUserDto);

        // Then
        assertThat(user.getNickname()).isEqualTo("updateNickname");
        assertThat(user.getEmail()).isEqualTo("testuser123@email.com");
        assertThat(user.getInfo()).isEqualTo("update User Info");
    }

    @Test
    @DisplayName("updateInfo - 이메일, 한줄소개만 변경 테스트")
    void test8() {
        // Given
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .email("uadateUser@email.com")
                .info("update User Info")
                .build();

        // When
        user.updateInfo(updateUserDto);

        // Then
        assertThat(user.getNickname()).isEqualTo("testNickname");
        assertThat(user.getEmail()).isEqualTo("uadateUser@email.com");
        assertThat(user.getInfo()).isEqualTo("update User Info");
    }

    @Test
    @DisplayName("updateInfo - 모두 변경하지 않는 테스트")
    void test9() {
        // Given
        UpdateUserDto updateUserDto = UpdateUserDto.builder().build();

        // When
        user.updateInfo(updateUserDto);

        // Then
        assertThat(user.getNickname()).isEqualTo("testNickname");
        assertThat(user.getEmail()).isEqualTo("testuser123@email.com");
        assertThat(user.getInfo()).isEqualTo("test User Info");
    }

    @Test
    @DisplayName("updatePassword - 비밀번호 변경 테스트")
    void test10() {
        // Given
        String newPassword = "newPassword234";

        // When
        user.updatePassword(newPassword);

        // Then
        assertThat(user.getPassword()).isEqualTo("newPassword234");
    }

    @Test
    @DisplayName("softDelete - 논리삭제 테스트")
    void test11() {
        // Given
        // softDelete() 가 실행될때

        // When
        user.softDelete();

        // Then
        assertThat(user.getStatus()).isEqualTo(StatusEnum.DELETED);
        assertThat(user.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("updateToken - 리플레시토큰 주입 테스트")
    void test12() {
        // Given
        String refreshToken = AuthEnum.GRANT_TYPE.getValue() + "refreshToken123";

        // When
        user.updateToken(refreshToken);

        // Then
        assertThat(user.getRefreshToken()).isEqualTo("Bearer refreshToken123");
    }

    // 이런것도 해야하나..?
    // 게터와 세터같은 테스트는 비용이 많이 발생하니 안하는것이 더 나을 것 같다.
    @Test
    @DisplayName("setExpired - 만료설정 테스트")
    void test13() {
        // Given
        boolean expiredTrue = true;

        // When
        user.setExpired(expiredTrue);

        // Then
        assertTrue(user.isExpired());
    }

    @Test
    @DisplayName("getExpired - 만료 값 확인 테스트")
    void test14() {
        // Given
        // getExpired() 가 실행될때

        // When
        user.getExpired();

        // Then
        assertThat(user.getExpired()).isFalse();
    }
}