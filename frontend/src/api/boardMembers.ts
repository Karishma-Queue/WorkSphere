import { apiClient } from './client';
import type { AddBoardMemberDTO } from '../types/api';
import type { AllBoardMemberDTO } from '../types/api';

export interface AddBoardMemberPayload {
  email: string;
  boardRole?: 'PROJECT_ADMIN' | 'PROJECT_MEMBER';
}

export const getBoardMembers = async (boardId: string) => {
  const { data } = await apiClient.get<AllBoardMemberDTO[]>(`/api/board/${boardId}/member`);
  return data;
};

export const addBoardMember = async (boardId: string, payload: AddBoardMemberPayload) => {
  const { data } = await apiClient.post(`/api/board/${boardId}/member`, payload);
  return data;
};

export const removeBoardMember = async (boardId: string, memberId: string) => {
  await apiClient.delete(`/api/board/${boardId}/member/${memberId}`);
};

