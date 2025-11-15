package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.*;
import com.karishma.worksphere.model.dto.request.CreateIssueDTO;
import com.karishma.worksphere.model.dto.request.UpdateIssueDTO;
import com.karishma.worksphere.model.dto.response.IssueResponse;
import com.karishma.worksphere.model.entity.*;
import com.karishma.worksphere.model.enums.IssueType;
import com.karishma.worksphere.model.enums.Priority;
import com.karishma.worksphere.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class IssueServiceTest {

    @Mock private BoardRepository boardRepository;
    @Mock private AuthRepository authRepository;
    @Mock private BoardMemberRepository boardMemberRepository;
    @Mock private WorkflowRepository workflowRepository;
    @Mock private WorkflowStatusRepository workflowStatusRepository;
    @Mock private IssueRepository issueRepository;

    @InjectMocks
    private IssueService issueService;

    private UUID boardId;
    private UUID issueId;
    private UUID reporterId;
    private UUID statusId;

    private Board board;
    private User reporter;
    private Auth auth;
    private Workflow workflow;
    private WorkflowStatus status;
    private Issue issue;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        boardId = UUID.randomUUID();
        issueId = UUID.randomUUID();
        reporterId = UUID.randomUUID();
        statusId = UUID.randomUUID();

        // Mock authentication user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@gmail.com", null)
        );

        reporter = User.builder()
                .userId(reporterId)
                .userName("ReporterUser")
                .build();

        auth = Auth.builder()
                .email("test@gmail.com")
                .user(reporter)
                .build();

        board = Board.builder()
                .boardId(boardId)
                .boardName("Dev Board")
                .build();

        workflow = Workflow.builder()
                .workflowId(UUID.randomUUID())
                .workflowName("Default Workflow")
                .board(board)
                .build();

        status = WorkflowStatus.builder()
                .statusId(statusId)
                .statusName("TO DO")
                .workflow(workflow)
                .isInitial(true)
                .build();

        issue = Issue.builder()
                .issueId(issueId)
                .board(board)
                .workflow(workflow)
                .status(status)
                .reporter(reporter)
                .summary("Test Summary")
                .priority(Priority.HIGH)
                .issue(IssueType.TASK)
                .build();
    }

    // ---------------------------------------------------------
    // CREATE ISSUE
    // ---------------------------------------------------------
    @Test
    void testCreateIssue() {

        CreateIssueDTO dto = new CreateIssueDTO();
        dto.setSummary("New Issue");
        dto.setPriority(Priority.MEDIUM);
        dto.setIssue_type(IssueType.TASK);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(authRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(auth));

        // No custom workflow → use default
        when(workflowRepository.findByBoard_BoardIdAndIssueType(boardId, IssueType.TASK))
                .thenReturn(null);

        when(workflowRepository.findByBoard_BoardIdAndIsDefaultTrue(boardId))
                .thenReturn(Optional.of(workflow));

        when(workflowStatusRepository
                .findByWorkflow_WorkflowIdAndIsInitialTrue(workflow.getWorkflowId()))
                .thenReturn(Optional.of(status));

        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        IssueResponse response = issueService.createIssue(boardId, dto);

        assertNotNull(response);
        assertEquals(issueId, response.getIssueId());
        assertEquals("Test Summary", response.getSummary());
        assertEquals(reporterId, response.getReporterId());
    }

    // ---------------------------------------------------------
    // GET ISSUE BY ID
    // ---------------------------------------------------------
    @Test
    void testGetIssueById() {

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));

        IssueResponse response = issueService.getIssueById(boardId, issueId);

        assertEquals(issueId, response.getIssueId());
        assertEquals("Test Summary", response.getSummary());
    }

    // ---------------------------------------------------------
    // UPDATE ISSUE
    // ---------------------------------------------------------
    @Test
    void testUpdateIssue() {

        UpdateIssueDTO dto = new UpdateIssueDTO();
        dto.setSummary("Updated Summary");

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        IssueResponse response = issueService.updateIssue(boardId, issueId, dto);

        assertEquals(issueId, response.getIssueId());
        assertEquals("Updated Summary", response.getSummary());
    }

    // ---------------------------------------------------------
    // DELETE ISSUE
    // ---------------------------------------------------------
    @Test
    void testDeleteIssue() {

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));

        assertDoesNotThrow(() -> issueService.deleteIssue(boardId, issueId));
        verify(issueRepository, times(1)).delete(issue);
    }

    // ---------------------------------------------------------
    // CHANGE STATUS
    // ---------------------------------------------------------
    @Test
    void testChangeStatus() {

        WorkflowStatus newStatus = WorkflowStatus.builder()
                .statusId(UUID.randomUUID())
                .statusName("DONE")
                .workflow(workflow)
                .build();

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(workflowStatusRepository.findById(newStatus.getStatusId()))
                .thenReturn(Optional.of(newStatus));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        IssueResponse response =
                issueService.changeIssueStatus(boardId, issueId, newStatus.getStatusId());

        assertEquals(issueId, response.getIssueId());
        assertEquals("TO DO", response.getStatus());  // old status because mock returns same entity
    }
}
