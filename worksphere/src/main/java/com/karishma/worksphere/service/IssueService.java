package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.exception.BadRequestException;
import com.karishma.worksphere.exception.NotFoundException;
import com.karishma.worksphere.model.dto.request.CreateIssueDTO;
import com.karishma.worksphere.model.dto.response.IssueResponse;
import com.karishma.worksphere.model.entity.*;
import com.karishma.worksphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final BoardMemberRepository boardMemberRepository;
    private final BoardRepository boardRepository;
    private final AuthRepository authRepository;
    private final WorkflowStatusRepository workflowStatusRepository;
    private final WorkflowRepository workflowRepository;
    private final IssueRepository issueRepository;
    public IssueResponse createIssue(UUID id, CreateIssueDTO request) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("No board exists with id " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Auth auth = authRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AuthenticationException("User not authenticated"));
        User reporter = auth.getUser();

        Workflow workflow = workflowRepository.findByBoard_BoardIdAndIssueType(id, request.getIssue_type());
        if (workflow == null) {
            workflow = workflowRepository.findByBoard_BoardIdAndIsDefaultTrue(id)
                    .orElseThrow(() -> new BadRequestException("No default workflow found"));
        }

        WorkflowStatus initialStatus = workflowStatusRepository
                .findByWorkflow_WorkflowIdAndIsInitialTrue(workflow.getWorkflowId())
                .orElseThrow(() -> new IllegalStateException("No initial status found"));

        User assignee = null;
        if (request.getAssignee_id() != null) {
            BoardMember boardMember = boardMemberRepository
                    .findByBoard_BoardIdAndBoardMemberId(id, request.getAssignee_id())
                    .orElseThrow(() -> new NotFoundException("No member exists in this board with this id"));

            assignee = boardMember.getUser();
        }

        Issue parent = null;
        if (request.getParent_id() != null) {
            parent = issueRepository.findById(request.getParent_id())
                    .orElseThrow(() -> new NotFoundException("Parent issue not found"));
        }

        Issue epic = null;
        if (request.getEpic_id() != null) {
            epic = issueRepository.findById(request.getEpic_id())
                    .orElseThrow(() -> new NotFoundException("Epic issue not found"));
        }

        Issue issue = Issue.builder()
                .board(board)
                .reporter(reporter)
                .workflow(workflow)
                .status(initialStatus)
                .issue(request.getIssue_type())
                .summary(request.getSummary())
                .priority(request.getPriority())
                .description(request.getDescription())
                .assignee(assignee)
                .parent(parent)
                .epic(epic)
                .due_date(request.getDue_Date())
                .build();

        Issue savedIssue = issueRepository.save(issue);
        return mapToIssueResponse(savedIssue);
    }
    private IssueResponse mapToIssueResponse(Issue issue) {
        return IssueResponse.builder()
                .issueId(issue.getIssue_id())
                .summary(issue.getSummary())
                .description(issue.getDescription())
                .priority(issue.getPriority().name())
                .issueType(issue.getIssue().name())
                .status(issue.getStatus().getStatusName())
                .boardName(issue.getBoard().getBoard_name())
                .workflowName(issue.getWorkflow().getWorkflowName())
                .dueDate(issue.getDue_date())
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdated_at())

                .reporterId(issue.getReporter().getUser_id())
                .reporterName(issue.getReporter().getUser_name())

                .assigneeId(issue.getAssignee() != null ? issue.getAssignee().getUser_id() : null)
                .assigneeName(issue.getAssignee() != null ? issue.getAssignee().getUser_name() : null)

                .parentId(issue.getParent() != null ? issue.getParent().getIssue_id() : null)
                .epicId(issue.getEpic() != null ? issue.getEpic().getIssue_id() : null)
                .build();
    }


}