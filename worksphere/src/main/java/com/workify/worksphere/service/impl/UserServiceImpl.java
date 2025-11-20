package com.workify.worksphere.service.impl;

import com.workify.worksphere.exception.NotFoundException;
import com.workify.worksphere.model.dto.request.UpdateUserDTO;
import com.workify.worksphere.model.dto.response.UserResponse;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.model.value.UserId;
import com.workify.worksphere.repository.UserRepository;
import com.workify.worksphere.service.CloudinaryService;
import com.workify.worksphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final CloudinaryService cloudinaryService;
  @Override
  public UserResponse getUser(String userId)
  {
    UserId userId1=UserId.of(userId);
    User user= userRepository.findByUserId(userId1)
        .orElseThrow(()->new NotFoundException("No user exist with id "+userId));
    return UserResponse.builder()
        .userId(user.getUserId().getValue())
        .userName(user.getUserName())
        .role(user.getRole().name())
        .jobTitle(user.getJobTitle())
        .department(user.getDepartment())
        .profilePictureUrl(user.getProfilePictureUrl())
        .createdAt(user.getCreatedAt())
        .build();

  }
  @Override
  public UserResponse updateUser(String userId, UpdateUserDTO dto, MultipartFile image) {
    UserId userId1=UserId.of(userId);

    User user = userRepository.findByUserId(userId1)
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
        .userId(user.getUserId().getValue())
        .userName(user.getUserName())
        .role(user.getRole().name())
        .jobTitle(user.getJobTitle())
        .department(user.getDepartment())
        .profilePictureUrl(user.getProfilePictureUrl())
        .createdAt(user.getCreatedAt())
        .build();

  }

}
