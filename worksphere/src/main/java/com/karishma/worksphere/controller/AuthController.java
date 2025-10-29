package com.karishma.worksphere.controller;

import com.karishma.worksphere.model.dto.request.SignupRequest;
import com.karishma.worksphere.model.dto.response.SignupResponse;
import com.karishma.worksphere.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestPart;

@RequiredArgsConstructor
public class AuthController {
   private final AuthService authService;
    public SignupResponse registerUser(@Valid @ModelAttribute SignupRequest request)
    {
        return authService.registerUser(request);
    }

}
