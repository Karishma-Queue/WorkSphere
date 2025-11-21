import { apiClient } from './client';
import type {
  AddStatusDTO,
  BoardWorkflowDTO,
  StatusResponse,
  TransitionRequest,
  TransitionResponse,
  WorkflowRequestDTO,
  WorkflowResponse,
  WorkflowUpdateDTO
} from '../types/api';

export const createWorkflow = async (boardId: string, payload: WorkflowRequestDTO) => {
  const { data } = await apiClient.post<WorkflowResponse>(`/api/workflow/${boardId}`, payload);
  return data;
};

export const getWorkflowsForBoard = async (boardId: string) => {
  const { data } = await apiClient.get<BoardWorkflowDTO[]>(`/api/workflow/board/${boardId}`);
  return data;
};

export const updateWorkflow = async (workflowId: string, payload: WorkflowUpdateDTO) => {
  const { data } = await apiClient.put(`/api/workflow/${workflowId}`, payload);
  return data;
};

export const deleteWorkflow = async (workflowId: string) => {
  await apiClient.delete(`/api/workflow/${workflowId}`);
};

export const addStatus = async (workflowId: string, payload: AddStatusDTO) => {
  const { data } = await apiClient.post<StatusResponse>(
    `/api/workflow/${workflowId}/status`,
    payload
  );
  return data;
};

export const deleteStatus = async (workflowId: string, statusId: string) => {
  await apiClient.delete(`/api/workflow/${workflowId}/status/${statusId}`);
};

export const addTransition = async (workflowId: string, payload: TransitionRequest) => {
  const { data } = await apiClient.post<TransitionResponse>(
    `/api/workflow/${workflowId}/transition`,
    payload
  );
  return data;
};

export const deleteTransition = async (workflowId: string, transitionId: string) => {
  await apiClient.delete(`/api/workflow/${workflowId}/transition/${transitionId}`);
};

