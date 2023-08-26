package com.example.studyspringbootwebservice.service.users;

import com.example.studyspringbootwebservice.domain.user.Role;
import com.example.studyspringbootwebservice.domain.user.User;
import com.example.studyspringbootwebservice.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UsersService {

    private final UserRepository userRepository;

    @Transactional
    public String update(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. email={}" + email));

        user.updateRole(Role.USER);
        log.info("user role={}", user.getRole());
        return email;
    }
}
