package com.sparta.areadevelopment.entity;


import com.sparta.areadevelopment.dto.UpdateUserDto;
import com.sparta.areadevelopment.enums.StatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * @String status : 탈퇴 여부를 저장 합니다. -> "Active", "Deleted"
 */

@Entity
@Getter
@Table(name = "users")
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private String info;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEnum status = StatusEnum.ACTIVE;

    private String refreshToken;

    //토큰 폐지
    @Column(nullable = false)
    private boolean expired = false;

    public User() {
    }

    // 암호화 한 password를 넣자
    public User(String username, String nickname, String password, String email,
            String info) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.info = info;
    }

    public void updateInfo(UpdateUserDto request) {
        if (request.getNickname() != null) {
            this.nickname = request.getNickname();
        }
        if (request.getEmail() != null) {
            this.email = request.getEmail();
        }
        if (request.getInfo() != null) {
            this.info = request.getInfo();
        }
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    // service 에서 탈퇴를 할 때 해당 메서드를 이용한다.
    public void softDelete() {
        this.status = StatusEnum.DELETED;
        this.setDeletedAt(LocalDateTime.now()); // Set the deletedAt timestamp when soft deleting
    }

    public void updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean getExpired() {
        return this.expired;
    }

    public void setExpired(boolean b) {
        this.expired = b;
    }

    public boolean isExpired() {
        return this.expired;
    }
}
