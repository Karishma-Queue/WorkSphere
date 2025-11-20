package com.workify.worksphere.security.aspect;

import com.workify.worksphere.exception.AuthenticationException;
import com.workify.worksphere.model.entity.Auth;
import com.workify.worksphere.model.entity.BoardMember;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.model.entity.Workflow;
import com.workify.worksphere.model.enums.BoardRole;
import com.workify.worksphere.model.value.BoardId;
import com.workify.worksphere.model.value.Email;
import com.workify.worksphere.repository.AuthRepository;
import com.workify.worksphere.repository.BoardMemberRepository;
import com.workify.worksphere.repository.WorkflowRepository;
import com.workify.worksphere.security.annotation.BoardIdParam;
import com.workify.worksphere.security.annotation.WorkflowIdParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

@Aspect

@Component
@Slf4j
@RequiredArgsConstructor
public class AllowOnlyProjAdminAspect {
    private final AuthRepository authRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final WorkflowRepository workflowRepository;

    @Before("@within(com.workify.worksphere.security.annotation.AllowOnlyProjAdmin) || " +
            "@annotation(com.workify.worksphere.security.annotation.AllowOnlyProjAdmin)")
    public void checkIfProjAdmin(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        MethodSignature methodSig = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = methodSig.getMethod().getParameters();

        String boardId = null;
        String workflowId = null;

        // First, check for direct board_id parameter
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(BoardIdParam.class)) {
                boardId = (String) args[i];
                break;
            }
        }
        System.out.println("---- DEBUG START ----");
   //TODO logged.info for error
        System.out.println("BoardId being checked: " + boardId);


        // If no board_id found, check for workflow_id parameter
        if (boardId == null) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].isAnnotationPresent(WorkflowIdParam.class)) {
                    workflowId = (String) args[i];
                    break;
                }
            }

            // If workflow_id found, fetch board_id from workflow
            if (workflowId != null) {
                final String finalWorkflowId = workflowId;
                Workflow workflow = workflowRepository.findById(finalWorkflowId)
                        .orElseThrow(() -> new RuntimeException("Workflow not found with id: " + finalWorkflowId));
                boardId = workflow.getBoard().getBoardId().getValue();
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

    private boolean checkCurrentUserIsAdmin(String boardId) {
        Authentication optional = SecurityContextHolder.getContext().getAuthentication();
        if (optional == null || !optional.isAuthenticated()) {
            throw new AuthenticationException("User not authenticated");
        }
        System.out.println("Logged-in email: " +optional.getName());


        Auth auth = authRepository.findByEmail( Email.of(optional.getName()))
                .orElseThrow(() -> new AuthenticationException("Authentication failed"));
        User user = auth.getUser();
        if (auth == null) {
            System.out.println("Auth NOT found in DB!");
        } else {
            System.out.println("Logged-in userId: " + auth.getUser().getUserId());
        }


        BoardMember boardMember = boardMemberRepository
                .findByBoard_BoardIdAndBoardRole(BoardId.of(boardId), BoardRole.PROJECT_ADMIN)
                .orElse(null);
        if (boardMember == null) {
            System.out.println("NO Project Admin found for this board!");
        } else {
            System.out.println("Board admin userId in DB: " + boardMember.getUser().getUserId());
            System.out.println("Board admin role: " + boardMember.getBoardRole());
        }

            if (boardMember == null) {
            return false;
        }

        return user.getUserId().equals(boardMember.getUser().getUserId());


    }
}