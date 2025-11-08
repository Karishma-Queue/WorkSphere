package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.*;
import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.model.dto.request.BoardRequestUpdateDTO;
import com.karishma.worksphere.model.dto.request.RejectRequestDTO;
import com.karishma.worksphere.model.dto.response.BoardDetailsDTO;
import com.karishma.worksphere.model.dto.response.BoardRequestResponse;
import com.karishma.worksphere.model.entity.*;
import com.karishma.worksphere.model.enums.BoardRole;
import com.karishma.worksphere.model.enums.Role;
import com.karishma.worksphere.model.enums.Status;
import com.karishma.worksphere.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

class BoardRequestServiceTest {

    @Mock
    private AuthRepository authRepository;
    @Mock
    private BoardRequestRepository boardRequestRepository;
    @Mock
    private BoardMemberRepository boardMemberRepository;
    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardRequestService boardRequestService;

    private BoardRequestDTO dto;
    private User adminUser;
    private User memberUser;
    private Auth adminAuth;
    private Auth memberAuth;
    private BoardRequest boardRequest;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .user_id(UUID.randomUUID())
                .user_name("Admin User")
                .role(Role.ADMIN)
                .job_title("Manager")
                .department("IT")
                .build();

        memberUser = User.builder()
                .user_id(UUID.randomUUID())
                .user_name("Member User")
                .role(Role.MEMBER)
                .job_title("Developer")
                .department("Engineering")
                .build();

        adminAuth = Auth.builder()
                .auth_id(UUID.randomUUID())
                .email("admin@test.com")
                .hashed_pass("hashed123")
                .user(adminUser)
                .build();

        memberAuth = Auth.builder()
                .auth_id(UUID.randomUUID())
                .email("member@test.com")
                .hashed_pass("hashed123")
                .user(memberUser)
                .build();

        boardRequest = BoardRequest.builder()
                .board_request_id(UUID.randomUUID())
                .board_request_name("Test Board")
                .board_request_key("TEST1")
                .description("Test board description")
                .justification("Need this board")
                .status(Status.PENDING)
                .requester(memberUser)
                .build();
        dto = BoardRequestDTO.builder()
                .board_request_name("New Board")
                .board_request_key("NB1")
                .description("Board for project X")
                .justification("To organize tasks")
                .build();


    }

    // --- helper for auth mocking ---
    private void mockAuthenticatedUser(String email, boolean authenticated) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        when(auth.isAuthenticated()).thenReturn(authenticated);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    // ✅ CREATE REQUEST
    @Test
    
    void testCreateRequest_Success() {
        when(authRepository.findByEmail("member@test.com"))
                .thenReturn(Optional.of(memberAuth));

        when(boardRequestRepository.save(any(BoardRequest.class)))
                .thenAnswer(invocation -> {
                    BoardRequest req = invocation.getArgument(0);
                    req.setBoard_request_id(UUID.randomUUID()); // ✅ Simulate ID from DB
                    return req;
                });

        ResponseEntity<?> response = boardRequestService.createRequest(dto, "member@test.com");

        assertEquals(201, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Board created successfully", body.get("message"));
        assertNotNull(body.get("boardId")); // ✅ ensure ID present
        verify(boardRequestRepository, times(1)).save(any(BoardRequest.class));
    }


    // ✅ APPROVE REQUEST SUCCESS
    @Test
    void testApproveRequest_Success() {
        mockAuthenticatedUser("admin@test.com", true);
        when(boardRequestRepository.findById(boardRequest.getBoard_request_id())).thenReturn(Optional.of(boardRequest));
        when(authRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminAuth));

        boardRequestService.approveRequest(boardRequest.getBoard_request_id());

        verify(boardRepository, times(1)).save(any(Board.class));
        verify(boardMemberRepository, times(1)).save(any(BoardMember.class));
        verify(boardRequestRepository, times(1)).save(boardRequest);
        assertEquals(Status.APPROVED, boardRequest.getStatus());
    }

    // ❌ APPROVE REQUEST - Already processed
    @Test
    void testApproveRequest_AlreadyProcessed() {
        boardRequest.setStatus(Status.REJECTED);
        when(boardRequestRepository.findById(boardRequest.getBoard_request_id())).thenReturn(Optional.of(boardRequest));
        assertThrows(BoardRequestException.class, () ->
                boardRequestService.approveRequest(boardRequest.getBoard_request_id()));
    }

    // ✅ REJECT REQUEST SUCCESS
    @Test
    void testRejectRequest_Success() {
        mockAuthenticatedUser("admin@test.com", true);
        when(boardRequestRepository.findById(boardRequest.getBoard_request_id())).thenReturn(Optional.of(boardRequest));
        when(authRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminAuth));

        RejectRequestDTO rejectDTO = new RejectRequestDTO();
        rejectDTO.setRejection_reason("Invalid data");

        boardRequestService.rejectRequest(boardRequest.getBoard_request_id(), rejectDTO);

        assertEquals(Status.REJECTED, boardRequest.getStatus());
        assertEquals("Invalid data", boardRequest.getRejection_reason());
        verify(boardRequestRepository, times(1)).save(boardRequest);
    }

    // ✅ MY REQUESTS
    @Test
    void testMyAllRequests_Success() {
        mockAuthenticatedUser("member@test.com", true);
        when(authRepository.findByEmail("member@test.com")).thenReturn(Optional.of(memberAuth));
        when(boardRequestRepository.findByRequester(memberUser)).thenReturn(List.of(boardRequest));

        List<BoardRequestResponse> responses = boardRequestService.myAllRequests();
        assertEquals(1, responses.size());
        assertEquals(boardRequest.getBoard_request_name(), responses.get(0).getName());
    }

    // ✅ UPDATE REQUEST SUCCESS
    @Test
    void testUpdateMyRequest_Success() {
        mockAuthenticatedUser("member@test.com", true);
        when(authRepository.findByEmail("member@test.com")).thenReturn(Optional.of(memberAuth));
        when(boardRequestRepository.findById(boardRequest.getBoard_request_id())).thenReturn(Optional.of(boardRequest));

        BoardRequestUpdateDTO updateDTO = new BoardRequestUpdateDTO();
        updateDTO.setBoard_request_name("Updated Name");

        boardRequestService.updateMyRequest(boardRequest.getBoard_request_id(), updateDTO);

        verify(boardRequestRepository, times(1)).save(boardRequest);
        assertEquals("Updated Name", boardRequest.getBoard_request_name());
    }

    // ❌ UPDATE REQUEST NOT OWNER
    @Test
    void testUpdateMyRequest_NotOwner() {
        mockAuthenticatedUser("admin@test.com", true);
        when(authRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminAuth));
        when(boardRequestRepository.findById(boardRequest.getBoard_request_id())).thenReturn(Optional.of(boardRequest));

        BoardRequestUpdateDTO dto = new BoardRequestUpdateDTO();
        assertThrows(AccessNotGivenException.class, () ->
                boardRequestService.updateMyRequest(boardRequest.getBoard_request_id(), dto));
    }

    // ✅ DELETE REQUEST SUCCESS
    @Test
    void testDeleteMyRequest_Success() {
        mockAuthenticatedUser("member@test.com", true);
        when(authRepository.findByEmail("member@test.com")).thenReturn(Optional.of(memberAuth));
        when(boardRequestRepository.findById(boardRequest.getBoard_request_id())).thenReturn(Optional.of(boardRequest));

        boardRequestService.deleteMyRequest(boardRequest.getBoard_request_id());
        verify(boardRequestRepository, times(1)).delete(boardRequest);
    }

    // ❌ DELETE REQUEST NOT OWNER
    @Test
    void testDeleteMyRequest_NotOwner() {
        mockAuthenticatedUser("admin@test.com", true);
        when(authRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminAuth));
        when(boardRequestRepository.findById(boardRequest.getBoard_request_id())).thenReturn(Optional.of(boardRequest));

        assertThrows(AccessNotGivenException.class, () ->
                boardRequestService.deleteMyRequest(boardRequest.getBoard_request_id()));
    }

    // ✅ GET MY REQUEST
    @Test
    void testGetMyRequest_Success() {
        mockAuthenticatedUser("member@test.com", true);
        when(authRepository.findByEmail("member@test.com")).thenReturn(Optional.of(memberAuth));
        when(boardRequestRepository.findById(boardRequest.getBoard_request_id())).thenReturn(Optional.of(boardRequest));

        BoardDetailsDTO dto = boardRequestService.getMyRequest(boardRequest.getBoard_request_id());
        assertEquals(boardRequest.getBoard_request_name(), dto.getBoard_request_name());
    }

    // ✅ ADMIN GET REQUEST BY ID
    @Test
    void testGetRequestById_Success_Admin() {
        mockAuthenticatedUser("admin@test.com", true);
        when(authRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminAuth));
        when(boardRequestRepository.findById(boardRequest.getBoard_request_id())).thenReturn(Optional.of(boardRequest));

        BoardDetailsDTO dto = boardRequestService.getRequestById(boardRequest.getBoard_request_id());
        assertEquals(boardRequest.getBoard_request_key(), dto.getBoard_request_key());
    }

    // ❌ NON-ADMIN TRYING TO ACCESS ADMIN METHOD
    @Test
    void testGetRequestById_AccessDenied() {
        mockAuthenticatedUser("member@test.com", true);
        when(authRepository.findByEmail("member@test.com")).thenReturn(Optional.of(memberAuth));

        assertThrows(AccessNotGivenException.class, () ->
                boardRequestService.getRequestById(boardRequest.getBoard_request_id()));
    }
}
