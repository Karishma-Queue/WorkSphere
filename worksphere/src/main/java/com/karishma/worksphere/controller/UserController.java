package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.UpdateUserDTO;
import com.karishma.worksphere.model.dto.response.UserResponse;
import com.karishma.worksphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
     @GetMapping("/{user_id}")
    public UserResponse getUser(@PathVariable String user_id)
     {
         return userService.getUser(user_id);
     }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse updateUser(
            @PathVariable String id,
            @RequestPart("data") UpdateUserDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return userService.updateUser(id, dto, image);
    }

}
