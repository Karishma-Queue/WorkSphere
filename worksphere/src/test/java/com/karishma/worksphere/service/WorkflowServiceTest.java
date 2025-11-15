package com.karishma.worksphere.service;

import com.karishma.worksphere.model.dto.request.AddStatusDTO;
import com.karishma.worksphere.model.dto.request.TransitionRequest;
import com.karishma.worksphere.model.dto.request.WorkflowRequestDTO;
import com.karishma.worksphere.model.dto.request.WorkflowUpdateDTO;
import com.karishma.worksphere.model.dto.response.*;
import com.karishma.worksphere.model.entity.*;
import com.karishma.worksphere.model.enums.IssueType;
import com.karishma.worksphere.model.enums.Role;
import com.karishma.worksphere.repository.*;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WorkflowServiceTest {

    @Mock private AuthRepository authRepository;
    @Mock private BoardRepository boardRepository;
    @Mock private WorkflowRepository workflowRepository;
    @Mock private WorkflowStatusRepository workflowStatusRepository;
    @Mock private WorkflowTransitionRepository workflowTransitionRepository;

    @InjectMocks
    private WorkflowService workflowService;

    private User user;
    private Auth authEntity;
    private Board board;
    private Workflow workflow;

    private UUID boardId;
    private UUID workflowId;
    private UUID statusId;
    private UUID toStatusId;
    private UUID transitionId;

    @BeforeEach
    void setup() {
        boardId = UUID.randomUUID();
        workflowId = UUID.randomUUID();
        statusId = UUID.randomUUID();
        toStatusId = UUID.randomUUID();
        transitionId = UUID.randomUUID();

        user = User.builder()
                .userId(UUID.randomUUID())
                .userName("Karishma")
                .role(Role.MEMBER)
                .jobTitle("Developer")
                .department("Engineering")
                .build();

        authEntity = Auth.builder()
                .authId(UUID.randomUUID())
                .email("karishma@example.com")
                .user(user)
                .build();

        board = Board.builder()
                .boardId(boardId)
                .boardName("Project Alpha")
                .build();

        workflow = Workflow.builder()
                .workflowId(workflowId)
                .workflowName("WF")
                .issueType(IssueType.TASK)
                .board(board)
                .createdBy(user)
                .build();
    }

    @Test
    void createWorkflow_Success() {
        WorkflowRequestDTO request = new WorkflowRequestDTO();
        request.setWorkflow_name("Dev WF");
        request.setIssue(IssueType.TASK);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("karishma@example.com");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(authRepository.findByEmail("karishma@example.com")).thenReturn(Optional.of(authEntity));
        when(workflowRepository.save(any(Workflow.class))).thenAnswer(x -> {
            Workflow w = x.getArgument(0);
            w.setWorkflowId(UUID.randomUUID());
            return w;
        });

        WorkflowResponse res = workflowService.createWorkflow(boardId, request);

        assertNotNull(res.getId());
        assertEquals("Dev WF", res.getWorkflow_name());
        assertEquals(IssueType.TASK, res.getIssue());

        verify(boardRepository).findById(boardId);
        verify(authRepository).findByEmail("karishma@example.com");
        verify(workflowRepository).save(any(Workflow.class));
    }

    @Test
    void getBoardWorkflows_Success() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(workflowRepository.findByBoard_BoardId(boardId)).thenReturn(List.of(workflow));

        List<BoardWorkflowDTO> res = workflowService.getBoardWorkflows(boardId);

        assertEquals(1, res.size());
        assertEquals("WF", res.get(0).getWorkflow_name());

        verify(workflowRepository).findByBoard_BoardId(boardId);
    }

    @Test
    void updateWorkflow_Success() {
        WorkflowUpdateDTO dto = new WorkflowUpdateDTO();
        dto.setWorkflow_name("Updated");
        dto.setIsDefault(true);

        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));
        when(workflowRepository.existsByBoard_BoardIdAndWorkflowName(workflow.getBoard().getBoardId(), "Updated")).thenReturn(false);
        when(workflowRepository.findByBoard_BoardIdAndIsDefaultTrue(workflow.getBoard().getBoardId())).thenReturn(Optional.empty());
        when(workflowRepository.save(any(Workflow.class))).thenAnswer(x -> x.getArgument(0));

        WorkflowUpdateResponse res = workflowService.updateWorkflow(workflowId, dto);

        assertEquals("Updated", res.getWorkflow_name());
        assertTrue(res.getIsDefault());

        verify(workflowRepository).findById(workflowId);
        verify(workflowRepository).save(any(Workflow.class));
    }

    @Test
    void deleteWorkflow_Success() {
        workflow.setDefault(false);
        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));
        when(workflowRepository.countByBoard_BoardId(workflow.getBoard().getBoardId())).thenReturn(2L);
        doNothing().when(workflowRepository).delete(workflow);

        workflowService.deleteWorkflow(workflowId);

        verify(workflowRepository).delete(workflow);
    }

    @Test
    void addStatus_Success() {

        AddStatusDTO dto = new AddStatusDTO();
        dto.setStatus_name("In Progress");

        when(workflowRepository.findById(workflowId))
                .thenReturn(Optional.of(workflow));

        when(workflowStatusRepository.existsByWorkflow_WorkflowIdAndStatusName(
                workflowId, "In Progress"
        )).thenReturn(false);

        WorkflowStatus saved = WorkflowStatus.builder()
                .statusId(statusId)     // THE FIX
                .statusName("In Progress")
                .workflow(workflow)
                .build();

        when(workflowStatusRepository.save(any())).thenReturn(saved);

        StatusResponse res = workflowService.addStatus(workflowId, dto);

        assertEquals("In Progress", res.getStatus_name());
    }

    @Test
    void deleteStatus_Success() {
        WorkflowStatus ws = WorkflowStatus.builder()
                .statusId(statusId)
                .statusName("To Do")
                .workflow(workflow)
                .build();

        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));
        when(workflowStatusRepository.findById(statusId)).thenReturn(Optional.of(ws));
        when(workflowStatusRepository.existsByWorkflow_WorkflowIdAndStatusId(workflowId, statusId)).thenReturn(true);
        doNothing().when(workflowStatusRepository).delete(ws);

        workflowService.deleteStatus(workflowId, statusId);

        verify(workflowStatusRepository).delete(ws);
    }

    @Test
    void addTransition_Success() {
        TransitionRequest dto = new TransitionRequest();
        dto.setFrom_status_id(statusId);
        dto.setTo_status_id(toStatusId);
        dto.setAllowedRoles(Set.of("ADMIN"));

        WorkflowStatus from = WorkflowStatus.builder()
                .statusId(statusId)
                .statusName("To Do")
                .workflow(workflow)
                .build();

        WorkflowStatus to = WorkflowStatus.builder()
                .statusId(toStatusId)
                .statusName("Done")
                .workflow(workflow)
                .build();

        WorkflowTransition transition = WorkflowTransition.builder()
                .workTransitionId(transitionId)
                .workflow(workflow)
                .fromStatus(from)
                .toStatus(to)
                .allowedRoles(Set.of("ADMIN"))
                .build();

        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));
        when(workflowStatusRepository.findById(statusId)).thenReturn(Optional.of(from));
        when(workflowStatusRepository.findById(toStatusId)).thenReturn(Optional.of(to));
        when(workflowTransitionRepository.existsByWorkflow_WorkflowIdAndFromStatus_StatusIdAndToStatus_StatusId(workflowId, statusId, toStatusId)).thenReturn(false);
        when(workflowTransitionRepository.save(any(WorkflowTransition.class))).thenReturn(transition);

        TransitionResponse res = workflowService.addTransition(workflowId, dto);

        assertEquals(workflowId, res.getWorkflow_id());
        assertEquals("To Do", res.getFrom_status());
        assertEquals("Done", res.getTo_status());

        verify(workflowTransitionRepository).save(any(WorkflowTransition.class));
    }

    @Test
    void deleteTransition_Success() {
        WorkflowTransition transition = WorkflowTransition.builder()
                .workTransitionId(transitionId)
                .workflow(workflow)
                .build();

        when(workflowRepository.findById(workflowId)).thenReturn(Optional.of(workflow));
        when(workflowTransitionRepository.findById(transitionId)).thenReturn(Optional.of(transition));
        doNothing().when(workflowTransitionRepository).delete(transition);

        workflowService.deleteTransition(workflowId, transitionId);

        verify(workflowTransitionRepository).delete(transition);
    }
}
