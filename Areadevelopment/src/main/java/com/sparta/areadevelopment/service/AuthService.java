package com.sparta.areadevelopment.service;

import com.sparta.areadevelopment.config.MailManager;

import com.sparta.areadevelopment.dto.TokenDto;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.enums.AuthEnum;
import com.sparta.areadevelopment.enums.StatusEnum;
import com.sparta.areadevelopment.jwt.TokenProvider;
import com.sparta.areadevelopment.repository.UserRepository;
import com.sparta.areadevelopment.util.SHA256Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그인 인증 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService implements LogoutHandler {

    /**
     * 관련 클래스 호출
     */
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MailManager mailManager;
    private static String magickey = "";

    /**
     * 로그인 메서드
     *
     * @param username
     * @param password
     * @return
     */
    @Transactional
    public TokenDto login(String username, String password) {
        if (!userRepository.existsByUsername(username)) {
            throw new UsernameNotFoundException(username);
        }
        Optional<User> user = userRepository.findUserByUsernameAndStatus(username,
                StatusEnum.ACTIVE);

        bCryptPasswordEncoder.matches(password, user.get().getPassword());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, password);
        // false가 활성화임
        user.get().setExpired(false);

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        TokenDto tokenDto = tokenProvider.generateToken(authentication);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        user.get().updateToken(tokenDto.getRefreshToken());

        return tokenDto;
    }

    /**
     * 토큰 재발급 메서드
     *
     * @param refreshToken
     * @return
     */
    @Transactional
    public TokenDto reissue(String refreshToken) {
        Optional<User> user = userRepository.findByRefreshToken(refreshToken);
        if (user != null && !user.get().getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("잘못된 토큰입니다.");
        } else if (user.get().isExpired()) {
            throw new RuntimeException("폐지된 토큰입니다.");
        }
        Authentication authentication = tokenProvider.getAuthentication(refreshToken.substring(7));
        TokenDto tokenDto = tokenProvider.generateToken(authentication);
        user.get().updateToken(tokenDto.getRefreshToken());
        return tokenDto;
    }

    /**
     * 로그아웃 메서드
     *
     * @param request
     * @param response
     * @param authentication
     */
    @Transactional
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        String authHeader = request.getHeader(AuthEnum.ACCESS_TOKEN.getValue());

        if (authHeader == null && !authHeader.startsWith(AuthEnum.GRANT_TYPE.getValue())) {
            throw new RuntimeException("알수 없는 access token.");
        }
        String accessToken = authHeader.substring(7);
        String username = tokenProvider.getUsername(accessToken);
        User refreshToken = userRepository.findByUsername(username).orElse(null);
        refreshToken.setExpired(true);
    }

    /**
     * 메일 전송 메서드
     *
     * @param email
     * @return
     */
    public ResponseEntity<String> sendMail(String email) {
        UUID uuid = UUID.randomUUID();
        String key = uuid.toString().substring(0, 7);
        String sub = "인증번호 메일 전송";
        String content = "인증번호 : " + key;
        mailManager.send(email, sub, content);
        magickey = SHA256Util.getEncrypt(key, email);
        log.info(magickey);
        return ResponseEntity.ok(key);
    }

    /**
     * 메일 인증 코드 검증 메서드
     *
     * @param key
     * @param email
     * @return
     */
    public ResponseEntity<String> checkMail(String key, String email) {
        String insertKey = SHA256Util.getEncrypt(key, email);
        if (!magickey.equals(insertKey)) {
            return ResponseEntity.status(403).body("잘못된 키 입력입니다");
        }
        return ResponseEntity.status(202).body("인증 완료");
    }
}


