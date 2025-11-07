package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.model.entity.BoardRequest;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.enums.Role;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.BoardRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardRequestServiceTest {
    @Mock
    AuthRepository authRepository;
    @Mock
    BoardRequestRepository boardRequestRepository;
    @InjectMocks
    BoardRequestService boardRequestService;
    private User user;
    private Auth auth;
    private BoardRequestDTO boardRequestDTO;

    @BeforeEach
    void setup() {
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
        boardRequestDTO = BoardRequestDTO.builder()
                .board_request_name("Test Board")
                .board_request_key("BOARD001")
                .justification("Desc")
                .description("Why needed")
                .build();


    }

    @Test
    void createRequest_Success() {
        when(authRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(auth));

        when(boardRequestRepository.save(any(BoardRequest.class)))
                .thenAnswer(invocation -> {
                    BoardRequest saved = invocation.getArgument(0);
                    saved.setBoard_request_id(UUID.fromString("5f8b6a12-8c3a-4e4e-90ec-93c7d61f77b0"));
                return saved;
                });

        ResponseEntity<?> response = boardRequestService.createRequest(boardRequestDTO, "test@example.com");

        assertEquals(201, response.getStatusCodeValue());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Board created successfully", body.get("message"));
        assertEquals("Test Board", body.get("boardName"));
        assertEquals((UUID.fromString("5f8b6a12-8c3a-4e4e-90ec-93c7d61f77b0")), body.get("boardId"));
        assertEquals("test@example.com", body.get("requestedBy"));

        verify(boardRequestRepository, times(1)).save(any(BoardRequest.class));
    }
    @Test
    void createRequest_Autheticationfailed()
    {
        when(authRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,()->boardRequestService.createRequest(boardRequestDTO,"test@example.com"));
        verify(authRepository,times(1)).findByEmail("test@example.com");
         verify(boardRequestRepository,never()).save(any(BoardRequest.class));

    }
    @Test

}
