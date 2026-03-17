package com.gbsw.meal.service;

import com.gbsw.meal.dto.request.LoginRequest;
import com.gbsw.meal.dto.request.RegisterRequest;
import com.gbsw.meal.dto.response.AuthResponse;
import com.gbsw.meal.entity.User;
import com.gbsw.meal.repository.UserRepository;
import com.gbsw.meal.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String ALLOWED_DOMAIN = "sc.gyo6.net";

    public void register(RegisterRequest request) {
        if (!request.getEmail().endsWith("@" + ALLOWED_DOMAIN)) {
            throw new IllegalArgumentException("학교 이메일(@sc.gyo6.net)만 가입 가능합니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.STUDENT)
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}
