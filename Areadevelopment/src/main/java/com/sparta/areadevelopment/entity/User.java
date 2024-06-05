package com.sparta.areadevelopment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import java.sql.Time;

/**
 * @String status : 탈퇴 여부를 저장 합니다. -> "Active", "Deleted"
 */
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String nickname;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    private String info;
    @Column(nullable = false)
    private String status = "Active";

    // 암호화 한 password를 넣자
    public void setUserInfo(String username, String password, String nickname, String email,
        String info) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.info = info;
    }

    // service 에서 탈퇴를 할 때 해당 메서드를 이용한다.
    public void softDelete() {
        this.status = "Deleted";
    }
}
