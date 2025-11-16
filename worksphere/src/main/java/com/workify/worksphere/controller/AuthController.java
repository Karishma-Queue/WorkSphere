package com.workify.worksphere.controller;

import com.workify.worksphere.model.dto.request.LoginRequest;
import com.workify.worksphere.model.dto.request.SignupRequest;
import com.workify.worksphere.model.dto.response.LoginResponse;
import com.workify.worksphere.model.dto.response.SignupResponse;
import com.workify.worksphere.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
   private final AuthService authService;
   @PostMapping("/signup")
    public SignupResponse registerUser(@Valid @ModelAttribute SignupRequest request)
    {
        return authService.registerUser(request);
    }
    @PostMapping("/login")
    public LoginResponse loginUser(@Valid @RequestBody  LoginRequest request)
    {
        return authService.loginUser(request);
    }
}
