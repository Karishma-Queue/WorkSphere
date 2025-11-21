import { apiClient } from './client';
import type { BoardResponse } from '../types/api';

export const getBoardsAsAdmin = async (userId: string) => {
  const { data } = await apiClient.get<BoardResponse[]>(`/api/board/proj-admin/${userId}`);
  return data;
};

export const getBoardsAsMember = async (userId: string) => {
  const { data } = await apiClient.get<BoardResponse[]>(`/api/board/proj-member/${userId}`);
  return data;
};

export const searchBoards = async (term: string) => {
  const { data } = await apiClient.get<BoardResponse[]>('/api/search', {
    params: { query: term }
  });
  return data;
};

