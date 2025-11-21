import { apiClient } from './client';
import type { BoardDetailsDTO, BoardRequestResponse, RequestStatus } from '../types/api';

export interface CreateBoardRequestPayload {
  board_request_name: string;
  board_request_key: string;
  description: string;
  justification: string;
}

export interface RejectBoardRequestPayload {
  rejection_reason: string;
}

export const createBoardRequest = async (payload: CreateBoardRequestPayload) => {
  const { data } = await apiClient.post('/api/project-request', payload);
  return data;
};

export const getAdminBoardRequests = async () => {
  const { data } = await apiClient.get('/api/project-request/admin');
  return data as BoardDetailsDTO[];
};

export const getAdminBoardRequestsByStatus = async (status: RequestStatus) => {
  const { data } = await apiClient.get<BoardDetailsDTO[]>('/api/project-request/admin/status', {
    params: { status }
  });
  return data;
};

export const getBoardRequestById = async (id: string) => {
  const { data } = await apiClient.get<BoardDetailsDTO>(`/api/project-request/admin/${id}`);
  return data;
};

export const approveBoardRequest = async (id: string) => {
  await apiClient.post(`/api/project-request/${id}/approve`);
};

export const rejectBoardRequest = async (id: string, payload: RejectBoardRequestPayload) => {
  await apiClient.post(`/api/project-request/${id}/reject`, payload);
};

export const getMyBoardRequests = async (status?: RequestStatus) => {
  const { data } = await apiClient.get<BoardDetailsDTO[]>('/api/project-request/my-request', {
    params: status ? { status } : undefined
  });
  return data;
};

export const updateMyBoardRequest = async (id: string, payload: CreateBoardRequestPayload) => {
  await apiClient.put(`/api/project-request/my-request/${id}/update`, payload);
};

export const deleteMyBoardRequest = async (id: string) => {
  await apiClient.delete(`/api/project-request/my-request/${id}/delete`);
};

