package com.karishma.worksphere.service;

import com.karishma.worksphere.exception.EmailAlreadyExists;
import com.karishma.worksphere.model.dto.request.LoginRequest;
import com.karishma.worksphere.model.dto.request.SignupRequest;
import com.karishma.worksphere.model.dto.response.LoginResponse;
import com.karishma.worksphere.model.dto.response.SignupResponse;
import com.karishma.worksphere.model.entity.Auth;
import com.karishma.worksphere.model.entity.User;
import com.karishma.worksphere.model.enums.Role;
import com.karishma.worksphere.repository.AuthRepository;
import com.karishma.worksphere.repository.UserRepository;
import com.karishma.worksphere.security.JwtUtil;
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
    @Mock
    JwtUtil jwtUtil;
    Auth auth;
    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    @BeforeEach
    void setUp()
    {

        MockMultipartFile mockFile = new MockMultipartFile(
                "profile_picture",
                "test.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        signupRequest=new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setUser_name("Test User");
        signupRequest.setDepartment("Engineering");
        signupRequest.setJob_title("Developer");
        signupRequest.setProfile_picture(mockFile);
        signupRequest.setRole(Role.MEMBER);
        loginRequest=new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        user = User.builder()
                .user_id(UUID.randomUUID())
                .user_name("Test User")
                .department("Engineering")
                .job_title("Developer")
                .role(Role.MEMBER)
                .profile_picture_url("https://fake.cloudinary.com/test.jpg")
                .build();
        auth = Auth.builder()
                .auth_id(UUID.randomUUID())
                .email("test@example.com")
                .hashed_pass("hashedPassword123")
                .user(user)
                .build();


    }
   @Test
    void testRegisterUser_Success(){
  when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
 when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("hashpassword123");
 when(cloudinaryService.uploadProfilePicture(any())).thenReturn("https://fake.cloudinary.com/test.jpg");
 //fake url stimulates success
when(userRepository.save(any(User.class))).thenReturn(user);
       when(authRepository.save(any(Auth.class))).thenReturn(auth);

          SignupResponse response = authService.registerUser(signupRequest);
       assertNotNull(response);
       assertEquals("Test User",response.getUserName());
       assertEquals("Engineering",response.getDepartment());
       assertEquals("Developer",response.getJobTitle());
       assertEquals("test@example.com",response.getEmail());
       assertEquals("https://fake.cloudinary.com/test.jpg", response.getProfilePictureUrl());
       verify(authRepository, times(1)).findByEmail("test@example.com");
       verify(passwordEncoder, times(1)).encode("password123");
       verify(cloudinaryService, times(1)).uploadProfilePicture(any());
       verify(userRepository, times(1)).save(any(User.class));
       verify(authRepository, times(1)).save(any(Auth.class));



   }
    @Test
    void testRegisterUser_EmailAlreadyExists_ThrowsException() {
        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(auth));

        assertThrows(EmailAlreadyExists.class, () -> authService.registerUser(signupRequest));

        verify(authRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, never()).encode(any());
        verify(cloudinaryService, never()).uploadProfilePicture(any());
        verify(userRepository, never()).save(any());
        verify(authRepository, never()).save(any());
    }
 @Test
    void testLoginUser_Success()
 {
     when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(auth));
     when(passwordEncoder.matches("password123","hashedPassword123")).thenReturn(true);
     when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("fake-jwt-token");
     LoginResponse response=authService.loginUser(loginRequest);
     assertNotNull(response);
     assertEquals("Test User", response.getUser_name());
     assertEquals("test@example.com", response.getEmail());
     assertEquals("fake-jwt-token", response.getToken());
     assertEquals(Role.MEMBER, response.getRole());
     verify(authRepository, times(1)).findByEmail("test@example.com");
     verify(passwordEncoder, times(1)).matches("password123", "hashedPassword123");
     verify(jwtUtil, times(1)).generateToken("test@example.com", "MEMBER");

 }

}
