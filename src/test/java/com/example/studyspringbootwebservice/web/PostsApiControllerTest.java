package com.example.studyspringbootwebservice.web;

import com.example.studyspringbootwebservice.domain.posts.Posts;
import com.example.studyspringbootwebservice.domain.posts.PostsRepository;
import com.example.studyspringbootwebservice.web.dto.PostsSaveRequestDto;
import com.example.studyspringbootwebservice.web.dto.PostsUpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    // 테스트 시작 전 MockMvc 인스턴스 생성
    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    public void tearDown() {
        postsRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")   // 인증된 모의 사용자
    public void Posts_등록() throws Exception {
        //given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        //when
        // post 요청 - postForEntity(url, post 요청 바디로 보낼 객체, 서버 응답을 변환할 타입)
        // ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        //then
        // assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        // assertThat(responseEntity.getBody()).isGreaterThan(0L); // 응답값이 있는지

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void Posts_수정() throws Exception {
        //given
        // Posts 생성해서 DB에 저장
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        Long updateId = savedPosts.getId();
        // 변경할 내용
        String expectedTitle = "title2";
        String expectedContent = "content2";

        // 변경할 내용을 담은 PostsUpdateRequestDto 생성
        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        // 테스트할 API의 엔드포인트
        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        // http 요청을 위한 httpEntity 객체 생성 (http 요청 바디에 requestDto 담아서 보냄)
        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        // exchange - put보다 유연함 (HttpEntity 포함)
        // ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        //then
        // assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        // assertThat(responseEntity.getBody()).isGreaterThan(0L); // 응답값이 있는지

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);
    }

}