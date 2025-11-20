package com.workify.worksphere.security.aspect;

import com.workify.worksphere.exception.AuthenticationException;
import com.workify.worksphere.exception.BadRequestException;
import com.workify.worksphere.model.entity.Auth;
import com.workify.worksphere.model.entity.Board;
import com.workify.worksphere.model.entity.BoardMember;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.model.value.BoardId;
import com.workify.worksphere.model.value.Email;
import com.workify.worksphere.repository.AuthRepository;
import com.workify.worksphere.repository.BoardMemberRepository;
import com.workify.worksphere.repository.BoardRepository;
import com.workify.worksphere.security.annotation.AllowProjAdminOrProjMember;
import com.workify.worksphere.security.annotation.BoardIdParam;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class AllowProjAdminOrProjMemberAspect {

  private final AuthRepository authRepository;
  private final BoardRepository boardRepository;
  private final BoardMemberRepository boardMemberRepository;

  @Before("@annotation(com.workify.worksphere.security.annotation.AllowProjAdminOrProjMember)")
  public void checkMemberOrAdminAccess(JoinPoint joinPoint) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Auth auth = authRepository
        .findByEmail(Email.of(authentication.getName()))
        .orElseThrow(() -> new AuthenticationException("User not authenticated"));
    User user = auth.getUser();

    String boardId = extractBoardId(joinPoint);

    Board board = boardRepository
        .findById(boardId)
        .orElseThrow(() -> new BadRequestException("Board not found"));

    BoardMember boardMember =
        boardMemberRepository
            .findByBoard_BoardIdAndUser_UserId(BoardId.of(boardId), user.getUserId())
            .orElseThrow(
                () ->
                    new AccessDeniedException("Access denied: You are not a member of this board"));

  }

  private String extractBoardId(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    Object[] args = joinPoint.getArgs();
    Annotation[][] paramAnnotations = method.getParameterAnnotations();

    for (int i = 0; i < paramAnnotations.length; i++) {
      for (Annotation annotation : paramAnnotations[i]) {
        if (annotation instanceof BoardIdParam) {
          return (String) args[i];
        }
      }
    }

    throw new IllegalStateException(
        "@AllowProjMemberOrAdmin requires a parameter annotated with @BoardIdParam");
  }
}