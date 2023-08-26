package com.example.studyspringbootwebservice.config.auth;

import com.example.studyspringbootwebservice.domain.user.Role;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
                .csrf().disable()                           // 토큰 검증 비활성화
                .headers().frameOptions().disable()         // h2-console 화면을 사용하기 위해 해당 옵션들 disable
                .and()
                    .authorizeHttpRequests(                 // http 요청에 대한 인증, 권한 설정 수행
                            request -> request
                                    .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()     // Forwad 요청에 대해 모든 사용자의 접근 허용
                                    .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                                    .requestMatchers("/api/v1/**").hasRole(Role.USER.name())      // 해당 경로에 대해서는 USER 권한을 가진 사용자만 접근 허용
                                    .anyRequest().authenticated()                                   // 이외의 모든 요청은 인증된 사용자만 접근 허용
                    )
                    .logout().logoutSuccessUrl("/")
                .and()
                    .oauth2Login()                              // oauth2.0 로그인 설정
                        .defaultSuccessUrl("/", true)
                        .userInfoEndpoint()                     // 로그인 성공한 사용자 정보를 가져올 때의 설정
                        .userService(customOAuth2UserService);  // 로그인 성공 시 수행할 service 등록
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/favicon.ico", "/resources/**", "/error");
    }
}
