package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.EmailAlreadyExists;
import com.karishma.worksphere.exception.InvalidCredentialsException;
import com.karishma.worksphere.exception.UserNotFoundException;
import com.karishma.worksphere.model.dto.request.SignupRequest;
import com.karishma.worksphere.model.dto.response.SignupResponse;
import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    public SignupResponse registerUser(SignupRequest request)
    {
       if(authRepository.findByEmail(request.getEmail()).isPresent())
       {
           throw new EmailAlreadyExists("Email already registered");
       }
    String hashedPassword= passwordEncoder.encode(request.getPassword());
    String url=cloudinaryService.uploadProfilePicture(request.getProfile_picture());
       User user=User.builder()
               .user_name(request.getUser_name())
               .department(request.getDepartment())
               .job_title(request.getJob_title())
               .role(request.getRole())
               .profile_picture_url(url)
               .build();
       userRepository.save(user);
       Auth auth=Auth.builder()
               .email(request.getEmail())
               .user(user)
               .hashed_pass(hashedPassword)
               .build();
       authRepository.save(auth);
       return new SignupResponse(user.getUser_name(),user.getJob_title(),user.getRole().toString(),user.getDepartment(),user.getProfile_picture_url(),auth.getEmail(),auth.getHashed_pass());
    }
    public SignupResponse loginUser(SignupRequest request)
    {
        Optional<Auth> optionalAuth = authRepository.findByEmail(request.getEmail());
       if(optionalAuth.isEmpty())
       {
           throw new UserNotFoundException("Account not created");
       }
       Auth auth=optionalAuth.get();
       User user=auth.getUser();
       boolean matches= passwordEncoder.matches(request.getPassword(), auth.getHashed_pass());
       if(!matches)
       {
           throw new InvalidCredentialsException("Incorrect password");
       }
       -
        return new LoginResponse(
                user.getUser_name(),
                user.getRole(),
                user.getProfile_picture_url(),
                auth.getEmail()
        );

    }
}
