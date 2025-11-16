package com.workify.worksphere.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {

    private String userId;
    private String userName;
    private String role;
    private String jobTitle;
    private String department;
    private String profilePictureUrl;

    private LocalDateTime createdAt;
}
