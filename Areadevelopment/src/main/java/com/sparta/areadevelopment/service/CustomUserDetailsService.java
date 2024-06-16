package com.sparta.areadevelopment.service;

import com.sparta.areadevelopment.entity.CustomUserDetails;
import com.sparta.areadevelopment.entity.User;
import com.sparta.areadevelopment.enums.StatusEnum;
import com.sparta.areadevelopment.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 유저디테일 서비스 커스텀
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 유저 디테일 서비스 메서드 오버라이딩
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
//    @Transactional
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (!Objects.equals(user.getStatus(), StatusEnum.ACTIVE)
                || user.isExpired()) {
            return null;
        }

        return new CustomUserDetails(user);
    }
}
