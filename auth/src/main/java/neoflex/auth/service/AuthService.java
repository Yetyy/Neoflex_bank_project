package neoflex.auth.service;

import lombok.RequiredArgsConstructor;
import neoflex.dto.AuthResponseDto;
import neoflex.dto.LoginRequestDto;
import neoflex.auth.entity.User;
import neoflex.auth.repository.UserRepository;
import neoflex.dto.RegisterRequestDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    // Предположим, что у вас есть компонент для генерации токенов, например, JwtTokenProvider
    private final neoflex.auth.security.JwtTokenProvider jwtTokenProvider;

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        // Поиск пользователя по email
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // Проверка пароля
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Неверный пароль");
        }

        // Генерация JWT-токена (или иного способа аутентификации)
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getId().toString());

        return new AuthResponseDto(token);
    }
    public AuthResponseDto register(RegisterRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getId().toString());
        return new AuthResponseDto(token);
    }
    // Метод register уже реализован для RegisterRequestDto
}
