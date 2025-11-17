package com.workify.worksphere.service.impl;

import com.workify.worksphere.exception.EmailAlreadyExists;
import com.workify.worksphere.exception.InvalidCredentialsException;
import com.workify.worksphere.exception.UserNotFoundException;
import com.workify.worksphere.model.dto.request.LoginRequest;
import com.workify.worksphere.model.dto.request.SignupRequest;
import com.workify.worksphere.model.dto.response.LoginResponse;
import com.workify.worksphere.model.dto.response.SignupResponse;
import com.workify.worksphere.model.entity.Auth;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.model.value.Email;
import com.workify.worksphere.repository.AuthRepository;
import com.workify.worksphere.repository.UserRepository;
import com.workify.worksphere.security.JwtUtil;
import com.workify.worksphere.service.AuthService;
import com.workify.worksphere.service.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final AuthRepository authRepository;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CloudinaryService cloudinaryService;

  @Override
  public SignupResponse registerUser(SignupRequest request) {

    if (authRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new EmailAlreadyExists("Email already registered");
    }

    String hashedPassword = passwordEncoder.encode(request.getPassword());
    String url = cloudinaryService.uploadProfilePicture(request.getProfile_picture());

    User user = User.builder()
        .userName(request.getUser_name())
        .department(request.getDepartment())
        .jobTitle(request.getJob_title())
        .role(request.getRole())
        .profilePictureUrl(url)
        .build();

    userRepository.save(user);
    Email email = Email.of(request.getEmail());

    Auth auth = Auth.builder()
        .email(email.getValue())
        .user(user)
        .hashedPass(hashedPassword)
        .build();

    authRepository.save(auth);

    return new SignupResponse(
        user.getUserName(),
        user.getJobTitle(),
        user.getRole().toString(),
        user.getDepartment(),
        user.getProfilePictureUrl(),
        auth.getEmail()
    );
  }

  @Override
  public LoginResponse loginUser(LoginRequest request) {

    Optional<Auth> optionalAuth = authRepository.findByEmail(request.getEmail());

    if (optionalAuth.isEmpty()) {
      throw new UserNotFoundException("Account not created");
    }

    Auth auth = optionalAuth.get();
    User user = auth.getUser();

    boolean matches = passwordEncoder.matches(request.getPassword(), auth.getHashedPass());

    if (!matches) {
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
