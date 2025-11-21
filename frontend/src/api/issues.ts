import { apiClient } from './client';
import type {
  CreateIssueDTO,
  CreateSprintDTO,
  IssueResponse,
  SprintResponse,
  UpdateIssueDTO
} from '../types/api';

export const getIssues = async (boardId: string) => {
  const { data } = await apiClient.get<IssueResponse[]>(`/api/boards/${boardId}/issues`);
  return data;
};

export const getBacklogIssues = async (boardId: string) => {
  const { data } = await apiClient.get(`/api/boards/${boardId}/issues/backlog`);
  return data as IssueResponse[];
};

export const getIssue = async (boardId: string, issueId: string) => {
  const { data } = await apiClient.get<IssueResponse>(
    `/api/boards/${boardId}/issues/${issueId}`
  );
  return data;
};

export const createIssue = async (boardId: string, payload: CreateIssueDTO) => {
  const { data } = await apiClient.post<IssueResponse>(
    `/api/boards/${boardId}/issues`,
    payload
  );
  return data;
};

export const updateIssue = async (boardId: string, issueId: string, payload: UpdateIssueDTO) => {
  const { data } = await apiClient.put<IssueResponse>(
    `/api/boards/${boardId}/issues/${issueId}`,
    payload
  );
  return data;
};

export const deleteIssue = async (boardId: string, issueId: string) => {
  await apiClient.delete(`/api/boards/${boardId}/issues/${issueId}`);
};

export const changeIssueStatus = async (
  boardId: string,
  issueId: string,
  statusId: string
) => {
  const { data } = await apiClient.put<IssueResponse>(
    `/api/boards/${boardId}/issues/${issueId}/status/${statusId}`
  );
  return data;
};

export const moveIssueToSprint = async (
  boardId: string,
  issueId: string,
  sprintId: string
) => {
  const { data } = await apiClient.patch<IssueResponse>(
    `/api/boards/${boardId}/issues/${issueId}/sprint/${sprintId}`
  );
  return data;
};

export const createSprint = async (boardId: string, payload: CreateSprintDTO) => {
  const { data } = await apiClient.post<SprintResponse>(
    `/api/boards/${boardId}/issues/sprints`,
    payload
  );
  return data;
};

export const getSprints = async (boardId: string) => {
  const { data } = await apiClient.get<SprintResponse[]>(
    `/api/boards/${boardId}/issues/sprints`
  );
  return data;
};

export const startSprint = async (boardId: string, sprintId: string) => {
  const { data } = await apiClient.post(
    `/api/boards/${boardId}/issues/sprints/${sprintId}/start`
  );
  return data as SprintResponse;
};

export const completeSprint = async (boardId: string, sprintId: string) => {
  const { data } = await apiClient.post(
    `/api/boards/${boardId}/issues/sprints/${sprintId}/complete`
  );
  return data as SprintResponse;
};

