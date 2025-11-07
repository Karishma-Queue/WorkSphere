package com.karishma.worksphere.service;

import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.model.entity.BoardRequest;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class BoardRequestServiceTest {
    private User user;
    private Auth auth;
    private BoardRequestDTO boardRequestDTO;
    @BeforeEach
    void setup()
    {
        MockMultipartFile mockFile = new MockMultipartFile(
                "profile_picture",
                "test.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        user = User.builder()
                .user_id(UUID.randomUUID())
                .user_name("Test User")
                .department("Engineering")
                .job_title("Developer")
                .role(Role.MEMBER)
                .profile_picture_url("https://fake.cloudinary.com/test.jpg")
                .build();
        auth = Auth.builder()
                .auth_id(UUID.randomUUID())
                .email("test@example.com")
                .hashed_pass("hashedPassword123")
                .user(user)
                .build();
        boardRequestDTO=BoardRequestDTO.builder()
                .board_request_name("Test Board")
                .board_request_key("BOARD001")
                .justification()


    }
    @Test

}
