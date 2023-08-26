package com.example.studyspringbootwebservice.web;

import com.example.studyspringbootwebservice.config.auth.LoginUser;
import com.example.studyspringbootwebservice.config.auth.dto.SessionUser;
import com.example.studyspringbootwebservice.service.users.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UsersService usersService;

    // role 변경
    @GetMapping("/users/update")
    public String userRoleUpdate(@LoginUser SessionUser user) {
        usersService.update(user.getEmail());

        return "redirect:/";

    }

}
