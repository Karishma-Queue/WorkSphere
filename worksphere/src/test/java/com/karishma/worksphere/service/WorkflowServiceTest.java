package com.karishma.worksphere.service;


import com.karishma.worksphere.model.dto.request.WorkflowRequestDTO;
import com.karishma.worksphere.model.dto.response.WorkflowResponse;
import com.karishma.worksphere.model.entity.*;
import com.karishma.worksphere.model.enums.BoardRole;
import com.karishma.worksphere.model.enums.BoardStatus;
import com.karishma.worksphere.model.enums.IssueType;
import com.karishma.worksphere.model.enums.Role;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.BoardRepository;
import com.karishma.worksphere.repository.WorkflowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WorkflowServiceTest {
    @Mock
    private AuthRepository authRepository;
    @Mock

    private BoardRepository boardRepository;
    @Mock
    private WorkflowRepository workflowRepository;
    @InjectMocks
    private WorkflowService workflowService;
    private Board board;
    private User user;
    private Auth authEntity;
    private BoardMember boardMember;
    private UUID boardId;
    @BeforeEach
    void setup()
    {
        boardId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        user = User.builder()
                .user_id(UUID.fromString("111e2222-e33b-44d5-b666-777788889999"))
                .user_name("Karishma")
                .role(Role.MEMBER) // correct: system role, not project role
                .job_title("Developer")
                .profile_picture_url(null)
                .department("Engineering")
                .build();

        authEntity = Auth.builder()
                .auth_id(UUID.randomUUID())
                .email("karishma@example.com")
                .user(user)
                .build();

         board = Board.builder()
                .board_id(boardId)
                .board_name("Project Alpha")
                .board_key("ALPHA")
                .description("Alpha project board")
                .createdBy(user)
                .build();


    }
    @Test
    void testCreateWorkflow_Success() {
        WorkflowRequestDTO request = new WorkflowRequestDTO();
        request.setWorkflow_name("Development Workflow");
        request.setIssue(IssueType.TASK);

        // Mock logged-in user authentication
        Authentication springAuth = mock(Authentication.class);
        when(springAuth.getName()).thenReturn("karishma@example.com");
        SecurityContextHolder.getContext().setAuthentication(springAuth);

        // Mock DB lookups
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(authRepository.findByEmail("karishma@example.com"))
                .thenReturn(Optional.of(authEntity));

        when(workflowRepository.save(any(Workflow.class)))
                .thenAnswer(invocation -> {
                    Workflow w = invocation.getArgument(0);
                    w.setWorkflowId(UUID.randomUUID());
                    return w;
                });

        WorkflowResponse response = workflowService.createWorkflow(boardId, request);

        assertNotNull(response.getId());
        assertEquals("Development Workflow", response.getWorkflow_name());
        assertEquals(IssueType.TASK, response.getIssue());

        verify(boardRepository).findById(boardId);
        verify(authRepository).findByEmail("karishma@example.com");
        verify(workflowRepository).save(any(Workflow.class));
    }

}
