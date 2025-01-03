package backend.service;

import backend.entity.User;
import backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자 주입
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(User user) {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            // 이메일 중복 확인
            throw new IllegalArgumentException("Email already registered");
        }

        if (user.getBirthDate() != null) {
            int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
            user.setAge(age);
        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);


    }

    public boolean authenticateUser(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return passwordEncoder.matches(rawPassword, user.getPassword());
        }
        return false;
    }

    /**
     * 이메일을 기반으로 사용자 정보를 가져옵니다.
     *
     * @param email 사용자의 이메일
     * @return User 객체 (없을 경우 RuntimeException 발생)
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
