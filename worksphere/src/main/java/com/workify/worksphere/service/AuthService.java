package com.workify.worksphere.service;

import com.workify.worksphere.model.dto.request.LoginRequest;
import com.workify.worksphere.model.dto.request.SignupRequest;
import com.workify.worksphere.model.dto.response.LoginResponse;
import com.workify.worksphere.model.dto.response.SignupResponse;

public interface AuthService {

  SignupResponse registerUser(SignupRequest request);

  LoginResponse loginUser(LoginRequest request);
}
