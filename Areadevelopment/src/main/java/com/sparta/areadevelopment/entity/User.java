package com.sparta.areadevelopment.entity;


import com.sparta.areadevelopment.dto.UpdateUserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @String status : 탈퇴 여부를 저장 합니다. -> "Active", "Deleted"
 */

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<Comment>();

    public void addComments(Comment comment) {
        comments.add(comment);
    }

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

    private StatusEnum status = StatusEnum.ACTIVE;

    private String refreshToken;

    public User() {
    }

    // 암호화 한 password를 넣자
    @Builder
    public User(String username, String nickname, String password, String email,
            String info) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.info = info;
    }

    public void updateProfile(UpdateUserDto user) {
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.info = user.getInfo();
        this.password = user.getPassword();
    }

    // service 에서 탈퇴를 할 때 해당 메서드를 이용한다.
    public void softDelete() {
        this.status = StatusEnum.DELETED;
        this.setDeletedAt(LocalDateTime.now()); // Set the deletedAt timestamp when soft deleting

    }

    public void updateValue(String refreshToken) {
       this.refreshToken = refreshToken;
    }
}
