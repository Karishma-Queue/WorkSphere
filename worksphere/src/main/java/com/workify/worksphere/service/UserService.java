package com.workify.worksphere.service;

import com.workify.worksphere.exception.NotFoundException;
import com.workify.worksphere.model.dto.request.UpdateUserDTO;
import com.workify.worksphere.model.dto.response.UserResponse;
import com.workify.worksphere.model.entity.User;
import com.workify.worksphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



public interface UserService {
  UserResponse getUser(String user_id);
  UserResponse updateUser(String id, UpdateUserDTO dto, MultipartFile image);

}
