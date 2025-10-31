package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.AuthenticationException;
import com.karishma.worksphere.exception.MemberOnlyException;
import com.karishma.worksphere.exception.UserNotFoundException;
import com.karishma.worksphere.model.dto.request.BoardRequestDTO;
import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.enums.Role;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.UserRepository;

import java.util.Optional;

public class BoardRequestService {
    private AuthRepository authRepository;
    private UserRepository userRepository;
    public  createRequest(BoardRequestDTO request,String email)
    {
        Optional <Auth> authOptional=authRepository.findByEmail(email);
        if(!authOptional.isPresent())
        {
            throw new AuthenticationException("Authentication not found");
        }
            Auth auth=authOptional.get();
            User user=auth.getUser();
            Optional<User> optionalUser= userRepository.findById(user.getUser_id()) ;
           if(!optionalUser.isPresent())
           {
               throw new UserNotFoundException("User not found");
           }
           else
           {
               User user1=optionalUser.get();
               if(!user1.getRole().equals(Role.MEMBER))
               {
                  return new MemberOnlyException("Only member is allowed");
               }
        


        }

    }
}
