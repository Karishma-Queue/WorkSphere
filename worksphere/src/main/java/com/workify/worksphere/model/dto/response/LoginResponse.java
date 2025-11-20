package com.workify.worksphere.model.dto.response;

import com.workify.worksphere.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String userName;
    private Role role;
    private String userId;
    private String profilePictureUrl;
    private String email;
    private String token;
}