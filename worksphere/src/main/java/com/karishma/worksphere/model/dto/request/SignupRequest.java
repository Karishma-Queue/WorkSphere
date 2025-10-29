package com.karishma.worksphere.model.dto.request;

import com.karishma.worksphere.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message="Name is required")
    private String user_name;
    @NotBlank(message ="Password is required")
    private String password;
    @NotBlank(message="Email is required")
    @Email(message="Please enter a valid email")
    private String email;
    @NotBlank(message="Role is required")
    private Role role;
    @NotBlank(message="Job Title is required")
    private String job_title;
    @NotBlank(message="Department is required")
    private String department;
    private MultipartFile profile_picture;

}
