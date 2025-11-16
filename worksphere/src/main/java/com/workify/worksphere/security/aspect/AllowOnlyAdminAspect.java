package com.workify.worksphere.security.aspect;

import com.workify.worksphere.exception.AccessNotGivenException;
import com.workify.worksphere.security.annotation.AllowOnlyAdmin;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AllowOnlyAdminAspect {
    @Before("@annotation(allowOnlyAdmin)")
    public void checkRole(JoinPoint joinPoint, AllowOnlyAdmin allowOnlyAdmin)
    {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        if(auth==null ||!auth.isAuthenticated())
        {
            throw new AccessNotGivenException("User must be logged in");

        }
        boolean isAdmin=auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(role->role.equals("ROLE_ADMIN"));
        if(!isAdmin)
        {
            throw new AccessNotGivenException("You are not authorized");
        }

    }
}
