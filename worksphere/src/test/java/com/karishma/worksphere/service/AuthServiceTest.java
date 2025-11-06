package com.karishma.worksphere.service;

import com.karishma.worksphere.model.dto.request.SignupRequest;
import com.karishma.worksphere.model.dto.response.SignupResponse;
import com.karishma.worksphere.model.enums.Role;
import com.karishma.worksphere.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    AuthService authService;
    @Mock
    AuthRepository authRepository;
    private SignupRequest signupRequest;

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

    }
   @Test
    void testRegisterUser_Success(){
  when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

          SignupResponse response = authService.registerUser(signupRequest);

   }

}
