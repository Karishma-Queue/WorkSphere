package com.workify.worksphere.service;

import com.workify.worksphere.exception.NotFoundException;
import com.workify.worksphere.model.dto.request.UpdateUserDTO;
import com.workify.worksphere.model.dto.response.UserResponse;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    public UserResponse getUser(String user_id)
    {
       User user= userRepository.findByUserId(user_id)
               .orElseThrow(()->new NotFoundException("No user exist with id "+user_id));
        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .role(user.getRole().name())
                .jobTitle(user.getJobTitle())
                .department(user.getDepartment())
                .profilePictureUrl(user.getProfilePictureUrl())
                .createdAt(user.getCreatedAt())
                .build();

    }
    public UserResponse updateUser(String id, UpdateUserDTO dto, MultipartFile image) {

        User user = userRepository.findByUserId(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (dto.getUserName() != null && !dto.getUserName().isBlank()) {
            user.setUserName(dto.getUserName());
        }

        if (dto.getJobTitle() != null && !dto.getJobTitle().isBlank()) {
            user.setJobTitle(dto.getJobTitle());
        }

        if (dto.getDepartment() != null && !dto.getDepartment().isBlank()) {
            user.setDepartment(dto.getDepartment());
        }

        if (image != null && !image.isEmpty()) {
            String url=cloudinaryService.uploadProfilePicture(image);
            user.setProfilePictureUrl(url);
        }

        userRepository.save(user);
        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .role(user.getRole().name())
                .jobTitle(user.getJobTitle())
                .department(user.getDepartment())
                .profilePictureUrl(user.getProfilePictureUrl())
                .createdAt(user.getCreatedAt())
                .build();

    }

}
