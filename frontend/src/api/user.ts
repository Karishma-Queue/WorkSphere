import { apiClient } from './client';
import type { UserResponse } from '../types/api';

export const getUserProfile = async (userId: string) => {
  const { data } = await apiClient.get<UserResponse>(`/user/${userId}`);
  return data;
};

export interface UpdateUserPayload {
  userName?: string;
  jobTitle?: string;
  department?: string;
  image?: File | null;
}

export const updateUserProfile = async (userId: string, payload: UpdateUserPayload) => {
  const form = new FormData();
  form.append(
    'data',
    new Blob(
      [
        JSON.stringify({
          userName: payload.userName,
          jobTitle: payload.jobTitle,
          department: payload.department
        })
      ],
      { type: 'application/json' }
    )
  );

  if (payload.image) {
    form.append('image', payload.image);
  }

  const { data } = await apiClient.put<UserResponse>(`/user/${userId}`, form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
  return data;
};

