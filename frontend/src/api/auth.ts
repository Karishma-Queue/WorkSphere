import { apiClient } from './client';
import type { LoginResponse, Role, SignupResponse } from '../types/api';

export interface SignupPayload {
  user_name: string;
  email: string;
  password: string;
  role: Role;
  job_title: string;
  department: string;
  profile_picture?: File | null;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export const login = async (payload: LoginPayload) => {
  const { data } = await apiClient.post<LoginResponse>('/api/auth/login', payload);
  return data;
};

export const signup = async (payload: SignupPayload) => {
  const form = new FormData();
  form.append('user_name', payload.user_name);
  form.append('email', payload.email);
  form.append('password', payload.password);
  form.append('role', payload.role);
  form.append('job_title', payload.job_title);
  form.append('department', payload.department);
  if (payload.profile_picture) {
    form.append('profile_picture', payload.profile_picture);
  }
  const { data } = await apiClient.post<SignupResponse>('/api/auth/signup', form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
  return data;
};

