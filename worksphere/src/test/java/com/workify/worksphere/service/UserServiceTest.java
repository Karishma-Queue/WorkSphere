//package com.workify.worksphere.service;
//
//import com.workify.worksphere.exception.NotFoundException;
//import com.workify.worksphere.model.dto.request.UpdateUserDTO;
//import com.workify.worksphere.model.dto.response.UserResponse;
//import com.workify.worksphere.model.entity.User;
//import com.workify.worksphere.model.enums.Role;
//import com.workify.worksphere.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private CloudinaryService cloudinaryService;
//
//    @InjectMocks
//    private UserService userService;
//
//    private UUID userId;
//    private User user;
//
//    @BeforeEach
//    void init() {
//        MockitoAnnotations.openMocks(this);
//
//        userId = UUID.randomUUID();
//        user = new User();
//        user.setUserId(userId);
//        user.setUserName("Karishma");
//        user.setRole(Role.MEMBER);
//        user.setJobTitle("Developer");
//        user.setDepartment("IT");
//        user.setProfilePictureUrl("old.jpg");
//        user.setCreatedAt(LocalDateTime.now());
//    }
//
//    // ------------------------------------------------------------
//    // GET USER — SUCCESS
//    // ------------------------------------------------------------
//    @Test
//    void getUser_success() {
//
//        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
//
//        UserResponse response = userService.getUser(userId);
//
//        assertEquals(userId, response.getUserId());
//        assertEquals("Karishma", response.getUserName());
//        assertEquals("MEMBER", response.getRole());
//        assertEquals("Developer", response.getJobTitle());
//        assertEquals("IT", response.getDepartment());
//        assertEquals("old.jpg", response.getProfilePictureUrl());
//    }
//
//    // ------------------------------------------------------------
//    // GET USER — NOT FOUND
//    // ------------------------------------------------------------
//    @Test
//    void getUser_notFound() {
//        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> userService.getUser(userId));
//    }
//
//    // ------------------------------------------------------------
//    // UPDATE USER — SUCCESS WITHOUT IMAGE
//    // ------------------------------------------------------------
//    @Test
//    void updateUser_success_withoutImage() {
//
//        UpdateUserDTO dto = new UpdateUserDTO();
//        dto.setUserName("NewName");
//        dto.setJobTitle("Manager");
//        dto.setDepartment("HR");
//
//        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
//        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        UserResponse response = userService.updateUser(userId, dto, null);
//
//        assertEquals("NewName", response.getUserName());
//        assertEquals("Manager", response.getJobTitle());
//        assertEquals("HR", response.getDepartment());
//        assertEquals("old.jpg", response.getProfilePictureUrl());  // unchanged
//    }
//
//    // ------------------------------------------------------------
//    // UPDATE USER — SUCCESS WITH IMAGE
//    // ------------------------------------------------------------
//    @Test
//    void updateUser_success_withImage() {
//
//        UpdateUserDTO dto = new UpdateUserDTO();
//        dto.setUserName("UpdatedUser");
//
//        MultipartFile mockImage = mock(MultipartFile.class);
//
//        when(mockImage.isEmpty()).thenReturn(false);
//        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
//
//        when(cloudinaryService.uploadProfilePicture(mockImage)).thenReturn("image123.jpg");
//
//        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        UserResponse response = userService.updateUser(userId, dto, mockImage);
//
//        assertEquals("UpdatedUser", response.getUserName());
//        assertEquals("image123.jpg", response.getProfilePictureUrl());
//    }
//
//}
