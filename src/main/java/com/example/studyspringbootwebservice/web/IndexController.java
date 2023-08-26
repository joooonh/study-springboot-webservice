package com.example.studyspringbootwebservice.web;

import com.example.studyspringbootwebservice.config.auth.LoginUser;
import com.example.studyspringbootwebservice.config.auth.dto.SessionUser;
import com.example.studyspringbootwebservice.service.posts.PostsService;
import com.example.studyspringbootwebservice.web.dto.PostsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user) {
        model.addAttribute("posts", postsService.findAllDesc());

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }

        return "index";
    }

    // 글 등록 페이지로 이동
    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
    }

    // 글 수정 페이지로 이동
    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);
        return "posts-update";
    }

}
