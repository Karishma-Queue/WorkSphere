package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.EmailAlreadyExists;
import com.karishma.worksphere.model.dto.request.SignupRequest;
import com.karishma.worksphere.model.dto.response.SignupResponse;
import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.enums.Role;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    AuthService authService;
    @Mock
    AuthRepository authRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    CloudinaryService cloudinaryService;
    User user;
    @Mock
    UserRepository userRepository;
    Auth auth;
    private SignupRequest signupRequest;
