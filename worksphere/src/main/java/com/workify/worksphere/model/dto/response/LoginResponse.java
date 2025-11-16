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
    private String user_name;
    private Role role;
    private String profile_picture_url;
    private String email;
    private String token;
}