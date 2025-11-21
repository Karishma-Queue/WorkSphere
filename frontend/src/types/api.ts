export type Role = 'ADMIN' | 'MEMBER' | 'VIEWER';
export type BoardRole = 'PROJECT_ADMIN' | 'PROJECT_MEMBER';
export type IssueType = 'EPIC' | 'STORY' | 'TASK' | 'BUG' | 'SUB_TASK' | 'UNKNOWN';
export type Priority = 'HIGHEST' | 'HIGH' | 'MEDIUM' | 'LOW' | 'UNKNOWN';
export type RequestStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED' | 'UNKNOWN';
export type SprintStatus = 'PLANNED' | 'ACTIVE' | 'COMPLETED';

export interface LoginResponse {
  userName: string;
  role: Role;
  userId: string;
  profilePictureUrl?: string;
  email: string;
  token: string;
}

export interface SignupResponse {
  userId: string;
  userName: string;
  jobTitle: string;
  role: string;
  department: string;
  profilePictureUrl?: string;
  email: string;
}

export interface BoardResponse {
  boardId: string;
  boardName: string;
  boardKey: string;
  description?: string;
  createdAt?: string;
  createdByUserId?: string;
  createdByUserName?: string;
}

export interface BoardRequestResponse {
  id: string;
  name: string;
  requesterName: {
    userName: string;
    email: string;
    userId: string;
  };
}

export interface BoardDetailsDTO {
  board_request_id: string;
  board_request_name: string;
  board_request_key: string;
  description: string;
  justification: string;
  requestedAt?: string;
  rejection_reason?: string;
  status: RequestStatus;
}

export interface IssueResponse {
  issueId: string;
  summary: string;
  description?: string;
  priority?: Priority | string;
  issueType?: IssueType | string;
  status?: string;
  boardName?: string;
  workflowName?: string;
  dueDate?: string;
  createdAt?: string;
  updatedAt?: string;
  reporterId?: string;
  reporterName?: string;
  assigneeId?: string;
  assigneeName?: string;
}

export interface SprintResponse {
  sprintId: string;
  springName: string;
  startDate?: string;
  endDate?: string;
  boardId?: string;
  sprintStatus: SprintStatus;
}

export interface WorkflowResponse {
  id: string;
  workflow_name: string;
  issue: IssueType;
  createdAt?: string;
}

export interface BoardWorkflowDTO {
  workflow_name: string;
  issue: IssueType;
  createdAt?: string;
}

export interface StatusResponse {
  id: string;
  status_name: string;
}

export interface TransitionResponse {
  from_status: string;
  to_status: string;
  workflow_id: string;
}

export interface UserResponse {
  userId: string;
  userName: string;
  role: Role | string;
  jobTitle?: string;
  department?: string;
  profilePictureUrl?: string;
  createdAt?: string;
}

export interface AllBoardMemberDTO {
  board_member_id: string;
  user_name: string;
  user_id: string;
  boardRole: BoardRole;
  joinedAt?: string;
}

export interface CreateIssueDTO {
  issue_type: IssueType;
  summary: string;
  priority: Priority;
  assignee_id?: string;
  description?: string;
  parent_id?: string;
  epic_id?: string;
  due_Date?: string;
}

export interface UpdateIssueDTO {
  summary?: string;
  description?: string;
  priority?: Priority;
  assignee_id?: string;
  due_date?: string;
}

export interface CreateSprintDTO {
  name: string;
  goal?: string;
  startDate?: string;
  endDate?: string;
}

export interface WorkflowRequestDTO {
  workflow_name: string;
  isDefault: boolean;
  issue: IssueType;
}

export interface WorkflowUpdateDTO {
  workflow_name?: string;
  isDefault?: boolean;
}

export interface AddStatusDTO {
  started?: boolean;
  ended?: boolean;
  is_final?: boolean;
  is_initial?: boolean;
  status_name: string;
}

export interface TransitionRequest {
  from_status_id: string;
  to_status_id: string;
  allowedRoles: string[];
}

