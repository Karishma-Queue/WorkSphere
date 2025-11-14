package com.karishma.worksphere.security.aspect;

import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.model.entity.BoardMember;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.entity.Workflow;
import com.karishma.worksphere.model.enums.BoardRole;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.BoardMemberRepository;
import com.karishma.worksphere.repository.WorkflowRepository;
import com.karishma.worksphere.security.annotation.BoardIdParam;
import com.karishma.worksphere.security.annotation.WorkflowIdParam;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class AllowOnlyProjAdminAspect {
    private final AuthRepository authRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final WorkflowRepository workflowRepository;

    @Before("@within(com.karishma.worksphere.security.annotation.AllowOnlyProjAdmin) || " +
            "@annotation(com.karishma.worksphere.security.annotation.AllowOnlyProjAdmin)")
    public void checkIfProjAdmin(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        MethodSignature methodSig = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = methodSig.getMethod().getParameters();

        UUID boardId = null;
        UUID workflowId = null;

        // First, check for direct board_id parameter
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(BoardIdParam.class)) {
                boardId = (UUID) args[i];
                break;
            }
        }

        // If no board_id found, check for workflow_id parameter
        if (boardId == null) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].isAnnotationPresent(WorkflowIdParam.class)) {
                    workflowId = (UUID) args[i];
                    break;
                }
            }

            // If workflow_id found, fetch board_id from workflow
            if (workflowId != null) {
                final UUID finalWorkflowId = workflowId;
                Workflow workflow = workflowRepository.findById(finalWorkflowId)
                        .orElseThrow(() -> new RuntimeException("Workflow not found with id: " + finalWorkflowId));
                boardId = workflow.getBoard().getBoard_id();
            }
        }

        if (boardId == null) {
            throw new RuntimeException("No @BoardIdParam or @WorkflowIdParam found in method arguments");
        }

        boolean isAdmin = checkCurrentUserIsAdmin(boardId);

        if (!isAdmin) {
            throw new RuntimeException("User is not a project admin");
        }
    }

    private boolean checkCurrentUserIsAdmin(UUID boardId) {
        Authentication optional = SecurityContextHolder.getContext().getAuthentication();
        if (optional == null || !optional.isAuthenticated()) {
            throw new AuthenticationException("User not authenticated");
        }

        Auth auth = authRepository.findByEmail(optional.getName())
                .orElseThrow(() -> new AuthenticationException("Authentication failed"));
        User user = auth.getUser();

        BoardMember boardMember = boardMemberRepository
                .findByBoard_BoardIdAndBoardRole(boardId, BoardRole.PROJECT_ADMIN)
                .orElse(null);

        if (boardMember == null) {
            return false;
        }

        return user.getUser_id().equals(boardMember.getUser().getUser_id());
    }
}