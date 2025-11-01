package com.karishma.worksphere.security.aspect;

import com.karishma.worksphere.exception.AccessNotGivenException;
import com.karishma.worksphere.security.annotation.AllowOnlyMember;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Aspect
@Component
public class AllowOnlyMemberAspect {
    @Before("@annotation(allowOnlyMember)")
    public void checkRole(JoinPoint joinPoint, AllowOnlyMember allowOnlyMember) {
     Authentication auth= SecurityContextHolder.getContext().getAuthentication();
     if(auth==null || !auth.isAuthenticated())
     {
         throw new AccessNotGivenException("User must be logged in");
     }
     boolean isAdmin=auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
             .anyMatch(role->role.equals("ROLE_ADMIN"));
        if (isAdmin) {
            throw new AccessNotGivenException("Admins are not allowed");
        }
        boolean isMember=auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(role->role.equals("ROLE_MEMBER"));
        if(!isMember)
        {
            throw new AccessNotGivenException("Only members are allowed");
        }


    }
}