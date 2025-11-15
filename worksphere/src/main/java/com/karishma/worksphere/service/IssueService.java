package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.*;
import com.karishma.worksphere.model.dto.request.CreateIssueDTO;
import com.karishma.worksphere.model.dto.request.UpdateIssueDTO;
import com.karishma.worksphere.model.dto.response.IssueResponse;
import com.karishma.worksphere.model.entity.*;
import com.karishma.worksphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final BoardMemberRepository boardMemberRepository;
    private final BoardRepository boardRepository;
    private final AuthRepository authRepository;
    private final WorkflowStatusRepository workflowStatusRepository;
    private final WorkflowRepository workflowRepository;
    private final IssueRepository issueRepository;

    // ✅ 1️⃣ Create Issue
    public IssueResponse createIssue(String boardId, CreateIssueDTO request) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException("No board exists with id " + boardId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Auth auth = authRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AuthenticationException("User not authenticated"));
        User reporter = auth.getUser();

        Workflow workflow = workflowRepository.findByBoard_BoardIdAndIssueType(boardId, request.getIssue_type());
        if (workflow == null) {
            workflow = workflowRepository.findByBoard_BoardIdAndIsDefaultTrue(boardId)
                    .orElseThrow(() -> new BadRequestException("No default workflow found"));
        }

        WorkflowStatus initialStatus = workflowStatusRepository
                .findByWorkflow_WorkflowIdAndIsInitialTrue(workflow.getWorkflowId())
                .orElseThrow(() -> new IllegalStateException("No initial status found"));

        User assignee = null;
        if (request.getAssignee_id() != null) {
            BoardMember boardMember = boardMemberRepository
                    .findByBoard_BoardIdAndBoardMemberId(boardId, request.getAssignee_id())
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
                .dueDate(request.getDue_Date())
                .build();

        Issue savedIssue = issueRepository.save(issue);
        return mapToIssueResponse(savedIssue);
    }

    // ✅ 2️⃣ Get all issues in a board
    public List<IssueResponse> getAllIssues(String boardId) {
        List<Issue> issues = issueRepository.findByBoard_BoardId(boardId);
        return issues.stream().map(this::mapToIssueResponse).toList();
    }

    // ✅ 3️⃣ Get single issue by ID
    public IssueResponse getIssueById(String boardId, String issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue not found"));
        if (!issue.getBoard().getBoardId().equals(boardId)) {
            throw new BadRequestException("Issue does not belong to this board");
        }
        return mapToIssueResponse(issue);
    }

    // ✅ 4️⃣ Update issue
    public IssueResponse updateIssue(String boardId, String issueId, UpdateIssueDTO request) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue not found"));
        if (!issue.getBoard().getBoardId().equals(boardId)) {
            throw new BadRequestException("Issue does not belong to this board");
        }

        if (request.getSummary() != null) issue.setSummary(request.getSummary());
        if (request.getDescription() != null) issue.setDescription(request.getDescription());
        if (request.getPriority() != null) issue.setPriority(request.getPriority());
        if (request.getDue_date() != null) issue.setDueDate(request.getDue_date());

        if (request.getAssignee_id() != null) {
            BoardMember boardMember = boardMemberRepository
                    .findByBoard_BoardIdAndBoardMemberId(boardId, request.getAssignee_id())
                    .orElseThrow(() -> new NotFoundException("Assignee not found in this board"));
            issue.setAssignee(boardMember.getUser());
        }

        Issue updated = issueRepository.save(issue);
        return mapToIssueResponse(updated);
    }

    // ✅ 5️⃣ Delete issue
    public void deleteIssue(String boardId, String issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue not found"));
        if (!issue.getBoard().getBoardId().equals(boardId)) {
            throw new BadRequestException("Issue does not belong to this board");
        }
        issueRepository.delete(issue);
    }

    // ✅ 6️⃣ Change issue status
    public IssueResponse changeIssueStatus(String boardId, String issueId, String statusId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new NotFoundException("Issue not found"));
        if (!issue.getBoard().getBoardId().equals(boardId)) {
            throw new BadRequestException("Issue does not belong to this board");
        }

        WorkflowStatus newStatus = workflowStatusRepository.findById(statusId)
                .orElseThrow(() -> new NotFoundException("Status not found"));

        if (!newStatus.getWorkflow().getWorkflowId().equals(issue.getWorkflow().getWorkflowId())) {
            throw new BadRequestException("Status does not belong to the same workflow");
        }

        issue.setStatus(newStatus);
        Issue updated = issueRepository.save(issue);
        return mapToIssueResponse(updated);
    }

    // ✅ Mapper method
    private IssueResponse mapToIssueResponse(Issue issue) {
        return IssueResponse.builder()
                .issueId(issue.getIssueId())
                .summary(issue.getSummary())
                .description(issue.getDescription())
                .priority(issue.getPriority().name())
                .issueType(issue.getIssue().name())
                .status(issue.getStatus().getStatusName())
                .boardName(issue.getBoard().getBoardName())
                .workflowName(issue.getWorkflow().getWorkflowName())
                .dueDate(issue.getDueDate())
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .reporterId(issue.getReporter().getUserId())
                .reporterName(issue.getReporter().getUserName())
                .assigneeId(issue.getAssignee() != null ? issue.getAssignee().getUserId() : null)
                .assigneeName(issue.getAssignee() != null ? issue.getAssignee().getUserName() : null)
                .parentId(issue.getParent() != null ? issue.getParent().getIssueId() : null)
                .epicId(issue.getEpic() != null ? issue.getEpic().getIssueId() : null)
                .build();
    }
}
