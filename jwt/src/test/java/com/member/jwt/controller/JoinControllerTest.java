package com.member.jwt.controller;

import com.member.jwt.dto.JoinDTO;
import com.member.jwt.service.JoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.*;

@WebMvcTest(JoinController.class)
public class JoinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JoinService joinService;

    @BeforeEach
    void setUp() {
        // 필요시 테스트 초기화 작업 추가
    }

    @Test
    public void testJoinProcess() throws Exception {
        // Given
        JoinDTO joinDTO = new JoinDTO();
        joinDTO.setUsername("testUser");
        joinDTO.setPassword("testPassword");

        // Mockito 설정: JoinService의 joinProcess 메서드가 호출되면 아무 동작도 하지 않도록 설정
        doNothing().when(joinService).joinProcess(Mockito.any(JoinDTO.class));

        // When & Then
        mockMvc.perform(post("/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testUser\", \"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

        // joinService의 메서드가 한 번 호출되었는지 확인
        verify(joinService, times(1)).joinProcess(Mockito.any(JoinDTO.class));
    }
}
