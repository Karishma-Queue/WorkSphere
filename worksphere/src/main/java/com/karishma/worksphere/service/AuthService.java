package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.EmailAlreadyExists;
import com.karishma.worksphere.exception.InvalidCredentialsException;
import com.karishma.worksphere.exception.UserNotFoundException;
import com.karishma.worksphere.model.dto.request.LoginRequest;
import com.karishma.worksphere.model.dto.request.SignupRequest;
import com.karishma.worksphere.model.dto.response.LoginResponse;
import com.karishma.worksphere.model.dto.response.SignupResponse;
import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.UserRepository;
import com.karishma.worksphere.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    @Transactional
    public SignupResponse registerUser(SignupRequest request)
    {
       if(authRepository.findByEmail(request.getEmail()).isPresent())
       {
           throw new EmailAlreadyExists("Email already registered");
       }
    String hashedPassword= passwordEncoder.encode(request.getPassword());
    String url=cloudinaryService.uploadProfilePicture(request.getProfile_picture());
       User user=User.builder()
               .userName(request.getUser_name())
               .department(request.getDepartment())
               .jobTitle(request.getJob_title())
               .role(request.getRole())
               .profilePictureUrl(url)
               .build();
       userRepository.save(user);

        Auth auth=Auth.builder()
               .email(request.getEmail())
               .user(user)
               .hashedPass(hashedPassword)
               .build();
       authRepository.save(auth);

        return new SignupResponse(user.getUserName(),user.getJobTitle(),user.getRole().toString(),user.getDepartment(),user.getProfilePictureUrl(),auth.getEmail());
    }
    public LoginResponse loginUser(LoginRequest request)
    {
        System.out.println("Fetching auth for email: " + request.getEmail());

        Optional<Auth> optionalAuth = authRepository.findByEmail(request.getEmail());

        if(optionalAuth.isEmpty())
       {
           throw new UserNotFoundException("Account not created");
       }
       Auth auth=optionalAuth.get();
       User user=auth.getUser();
       boolean matches= passwordEncoder.matches(request.getPassword(), auth.getHashedPass());
       if(!matches)
       {
           throw new InvalidCredentialsException("Incorrect password");
       }

               String token = jwtUtil.generateToken(auth.getEmail(), user.getRole().toString());

        return LoginResponse.builder()
                .user_name(user.getUserName())
                .role(user.getRole())
                .profile_picture_url(user.getProfilePictureUrl())
                .email(auth.getEmail())
                .token(token)
                .build();
    }
}
