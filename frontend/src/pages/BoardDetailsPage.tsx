import { useState } from 'react';
import { Link, useLocation, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  createIssue,
  createSprint,
  getIssues,
  getSprints,
  startSprint,
  completeSprint
} from '../api/issues';
import { getBoardMembers, addBoardMember, removeBoardMember } from '../api/boardMembers';
import { getWorkflowsForBoard } from '../api/workflows';
import type {
  BoardResponse,
  CreateIssueDTO,
  CreateSprintDTO,
  IssueType,
  Priority,
  BoardRole
} from '../types/api';
import { SectionHeader } from '../components/common/SectionHeader';
import { GlassCard } from '../components/common/Card';
import { Input } from '../components/forms/Input';
import { Select } from '../components/forms/Select';
import { Textarea } from '../components/forms/Textarea';
import { Button } from '../components/common/Button';
import { EmptyState } from '../components/common/EmptyState';
import { formatDate } from '../utils/format';
import toast from 'react-hot-toast';
import {
  ArrowLeft,
  Plus,
  Users,
  Workflow,
  Bug,
  CalendarClock,
  BadgeCheck,
  Trash2
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const issueTypes: IssueType[] = ['EPIC', 'STORY', 'TASK', 'BUG', 'SUB_TASK'];
const priorities: Priority[] = ['HIGHEST', 'HIGH', 'MEDIUM', 'LOW'];
const boardRoles: BoardRole[] = ['PROJECT_ADMIN', 'PROJECT_MEMBER'];

const BoardDetailsPage = () => {
  const { boardId } = useParams();
  const location = useLocation();
  const board = location.state as BoardResponse | undefined;
  const queryClient = useQueryClient();
  const { user } = useAuth();
  const canManageBoard = user?.role !== 'VIEWER';

  const [issueForm, setIssueForm] = useState<CreateIssueDTO>({
    issue_type: 'TASK',
    priority: 'MEDIUM',
    summary: '',
    description: '',
    due_Date: ''
  });
  const [memberForm, setMemberForm] = useState({ email: '', boardRole: 'PROJECT_MEMBER' });
  const [sprintForm, setSprintForm] = useState<CreateSprintDTO>({
    name: '',
    goal: '',
    startDate: '',
    endDate: ''
  });

  const issuesQuery = useQuery({
    queryKey: ['issues', boardId],
    queryFn: () => getIssues(boardId!),
    enabled: Boolean(boardId)
  });

  const membersQuery = useQuery({
    queryKey: ['board-members', boardId],
    queryFn: () => getBoardMembers(boardId!),
    enabled: Boolean(boardId)
  });

  const workflowsQuery = useQuery({
    queryKey: ['workflows', boardId],
    queryFn: () => getWorkflowsForBoard(boardId!),
    enabled: Boolean(boardId)
  });

  const sprintsQuery = useQuery({
    queryKey: ['sprints', boardId],
    queryFn: () => getSprints(boardId!),
    enabled: Boolean(boardId)
  });

  const createIssueMutation = useMutation({
    mutationFn: () => createIssue(boardId!, issueForm),
    onSuccess: () => {
      toast.success('Issue created');
      setIssueForm({ issue_type: 'TASK', priority: 'MEDIUM', summary: '', description: '', due_Date: '' });
      queryClient.invalidateQueries({ queryKey: ['issues', boardId] });
    },
    onError: () => toast.error('Unable to create issue')
  });

  const addMemberMutation = useMutation({
    mutationFn: () => addBoardMember(boardId!, memberForm),
    onSuccess: () => {
      toast.success('Member added');
      setMemberForm({ email: '', boardRole: 'PROJECT_MEMBER' });
      queryClient.invalidateQueries({ queryKey: ['board-members', boardId] });
    },
    onError: () => toast.error('Unable to add member')
  });

  const removeMemberMutation = useMutation({
    mutationFn: (memberId: string) => removeBoardMember(boardId!, memberId),
    onSuccess: () => {
      toast.success('Member removed');
      queryClient.invalidateQueries({ queryKey: ['board-members', boardId] });
    },
    onError: () => toast.error('Unable to remove member')
  });

  const createSprintMutation = useMutation({
    mutationFn: () => createSprint(boardId!, sprintForm),
    onSuccess: () => {
      toast.success('Sprint created');
      setSprintForm({ name: '', goal: '', startDate: '', endDate: '' });
      queryClient.invalidateQueries({ queryKey: ['sprints', boardId] });
    },
    onError: () => toast.error('Unable to create sprint')
  });

  const startSprintMutation = useMutation({
    mutationFn: (sprintId: string) => startSprint(boardId!, sprintId),
    onSuccess: () => {
      toast.success('Sprint started');
      queryClient.invalidateQueries({ queryKey: ['sprints', boardId] });
    },
    onError: () => toast.error('Unable to start sprint')
  });

  const completeSprintMutation = useMutation({
    mutationFn: (sprintId: string) => completeSprint(boardId!, sprintId),
    onSuccess: () => {
      toast.success('Sprint completed');
      queryClient.invalidateQueries({ queryKey: ['sprints', boardId] });
    },
    onError: () => toast.error('Unable to complete sprint')
  });

  const pageTitle = board?.boardName ?? `Board ${boardId}`;

  if (!boardId) {
    return (
      <EmptyState title="Board not found" description="Return to the dashboard to choose another." />
    );
  }

  return (
    <div className="space-y-10">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div className="space-y-2">
          <Link to="/dashboard" className="inline-flex items-center gap-2 text-sm text-slate-400">
            <ArrowLeft className="h-4 w-4" />
            Back to boards
          </Link>
          <h1 className="text-3xl font-semibold text-white">{pageTitle}</h1>
          <p className="text-sm text-slate-400">
            Key {board?.boardKey ?? boardId} · Created {formatDate(board?.createdAt)}
          </p>
        </div>
      </div>

      {canManageBoard ? (
        <div className="grid gap-6 lg:grid-cols-[2fr,1fr]">
          <GlassCard className="space-y-4">
            <SectionHeader
              title="Create issue"
              description="Track deliveries by filing tasks, epics or bugs."
              icon={<Bug className="h-5 w-5" />}
            />
            <form
              onSubmit={(event) => {
                event.preventDefault();
                createIssueMutation.mutate();
              }}
              className="grid gap-4 md:grid-cols-2"
            >
              <label className="text-sm text-slate-300">
                Summary
                <Input
                  required
                  value={issueForm.summary}
                  onChange={(event) =>
                    setIssueForm((prev) => ({ ...prev, summary: event.target.value }))
                  }
                  className="mt-2"
                />
              </label>
              <label className="text-sm text-slate-300">
                Issue type
                <Select
                  value={issueForm.issue_type}
                  onChange={(event) =>
                    setIssueForm((prev) => ({
                      ...prev,
                      issue_type: event.target.value as IssueType
                    }))
                  }
                  className="mt-2"
                >
                  {issueTypes.map((type) => (
                    <option key={type} value={type}>
                      {type}
                    </option>
                  ))}
                </Select>
              </label>
              <label className="text-sm text-slate-300">
                Priority
                <Select
                  value={issueForm.priority}
                  onChange={(event) =>
                    setIssueForm((prev) => ({
                      ...prev,
                      priority: event.target.value as Priority
                    }))
                  }
                  className="mt-2"
                >
                  {priorities.map((priority) => (
                    <option key={priority} value={priority}>
                      {priority}
                    </option>
                  ))}
                </Select>
              </label>
              <label className="text-sm text-slate-300">
                Due date
                <Input
                  type="date"
                  value={issueForm.due_Date ?? ''}
                  onChange={(event) =>
                    setIssueForm((prev) => ({ ...prev, due_Date: event.target.value }))
                  }
                  className="mt-2"
                />
              </label>
              <label className="text-sm text-slate-300 md:col-span-2">
                Description
                <Textarea
                  rows={3}
                  value={issueForm.description ?? ''}
                  onChange={(event) =>
                    setIssueForm((prev) => ({ ...prev, description: event.target.value }))
                  }
                  className="mt-2"
                />
              </label>
              <div className="md:col-span-2 flex justify-end">
                <Button type="submit" loading={createIssueMutation.isPending}>
                  <Plus className="h-4 w-4" />
                  Create issue
                </Button>
              </div>
            </form>
          </GlassCard>

          <GlassCard className="space-y-4">
            <SectionHeader
              title="Add board member"
              description="Invite collaborators by email and role."
              icon={<Users className="h-5 w-5" />}
            />
            <form
              className="space-y-3"
              onSubmit={(event) => {
                event.preventDefault();
                addMemberMutation.mutate();
              }}
            >
              <label className="text-sm text-slate-300">
                Work email
                <Input
                  type="email"
                  required
                  value={memberForm.email}
                  onChange={(event) =>
                    setMemberForm((prev) => ({ ...prev, email: event.target.value }))
                  }
                  className="mt-2"
                />
              </label>
              <label className="text-sm text-slate-300">
                Role
                <Select
                  value={memberForm.boardRole}
                  onChange={(event) =>
                    setMemberForm((prev) => ({
                      ...prev,
                      boardRole: event.target.value as BoardRole
                    }))
                  }
                  className="mt-2"
                >
                  {boardRoles.map((role) => (
                    <option key={role} value={role}>
                      {role.replace('_', ' ')}
                    </option>
                  ))}
                </Select>
              </label>
              <Button type="submit" className="w-full" loading={addMemberMutation.isPending}>
                Invite member
              </Button>
            </form>
          </GlassCard>
        </div>
      ) : (
        <GlassCard className="space-y-2">
          <SectionHeader
            title="Read-only board"
            description="You can inspect this board but need a higher role to create issues or manage members."
            icon={<Bug className="h-5 w-5" />}
          />
          <p className="text-sm text-slate-400">
            Ask an admin to upgrade your role or head to the Build Center once you are a member.
          </p>
        </GlassCard>
      )}

      <GlassCard className="space-y-4">
        <SectionHeader
          title="Issues in board"
          description="Every open work item inside this board."
          icon={<BadgeCheck className="h-5 w-5" />}
        />
        {issuesQuery.data?.length ? (
          <div className="overflow-hidden rounded-2xl border border-white/5">
            <table className="w-full text-left text-sm text-slate-300">
              <thead className="bg-white/5 text-xs uppercase text-slate-400">
                <tr>
                  <th className="px-4 py-3">Summary</th>
                  <th className="px-4 py-3">Type</th>
                  <th className="px-4 py-3">Priority</th>
                  <th className="px-4 py-3">Status</th>
                  <th className="px-4 py-3">Assignee</th>
                  <th className="px-4 py-3">Due</th>
                </tr>
              </thead>
              <tbody>
                {issuesQuery.data.map((issue) => (
                  <tr key={issue.issueId} className="border-t border-white/5">
                    <td className="px-4 py-3 font-medium text-white">{issue.summary}</td>
                    <td className="px-4 py-3">{issue.issueType ?? '—'}</td>
                    <td className="px-4 py-3">{issue.priority ?? '—'}</td>
                    <td className="px-4 py-3">{issue.status ?? '—'}</td>
                    <td className="px-4 py-3">{issue.assigneeName ?? 'Unassigned'}</td>
                    <td className="px-4 py-3">{formatDate(issue.dueDate)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <EmptyState title="No issues yet" description="Create one to get started." />
        )}
      </GlassCard>

      <div className="grid gap-6 lg:grid-cols-2">
        <GlassCard className="space-y-4">
          <SectionHeader
            title="Board members"
            description="Track every collaborator along with their board roles."
            icon={<Users className="h-5 w-5" />}
          />
          {membersQuery.data?.length ? (
            <div className="space-y-3">
              {membersQuery.data.map((member) => (
                <div
                  key={member.board_member_id}
                  className="flex items-center justify-between rounded-xl border border-white/5 bg-white/5 p-3 text-sm"
                >
                  <div>
                    <p className="font-medium text-white">{member.user_name}</p>
                    <p className="text-xs text-slate-400">
                      {member.boardRole.replace('_', ' ')} · Joined {formatDate(member.joinedAt)}
                    </p>
                  </div>
                  {canManageBoard && (
                    <Button
                      variant="ghost"
                      onClick={() => removeMemberMutation.mutate(member.board_member_id)}
                      disabled={removeMemberMutation.isPending}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <EmptyState title="No collaborators yet" description="Invite contributors above." />
          )}
        </GlassCard>

        <GlassCard className="space-y-4">
          <SectionHeader
            title="Workflows"
            description="Statuses & transitions available in this board."
            icon={<Workflow className="h-5 w-5" />}
          />
          {workflowsQuery.data?.length ? (
            <div className="space-y-3">
              {workflowsQuery.data.map((workflow) => (
                <div key={workflow.workflow_name} className="rounded-xl border border-white/5 p-3">
                  <p className="font-medium text-white">{workflow.workflow_name}</p>
                  <p className="text-xs text-slate-400">
                    Applies to {workflow.issue} · Created {formatDate(workflow.createdAt)}
                  </p>
                </div>
              ))}
            </div>
          ) : (
            <EmptyState title="No workflows yet" description="Create workflows via the API." />
          )}
        </GlassCard>
      </div>

      <GlassCard className="space-y-4">
        <SectionHeader
          title="Sprints"
          description="Plan and execute iterations."
          icon={<CalendarClock className="h-5 w-5" />}
        />
        {canManageBoard ? (
          <form
            className="grid gap-4 md:grid-cols-4"
            onSubmit={(event) => {
              event.preventDefault();
              createSprintMutation.mutate();
            }}
          >
            <label className="text-sm text-slate-300 md:col-span-1">
              Sprint name
              <Input
                required
                value={sprintForm.name}
                onChange={(event) =>
                  setSprintForm((prev) => ({ ...prev, name: event.target.value }))
                }
                className="mt-2"
              />
            </label>
            <label className="text-sm text-slate-300 md:col-span-1">
              Goal
              <Input
                value={sprintForm.goal ?? ''}
                onChange={(event) =>
                  setSprintForm((prev) => ({ ...prev, goal: event.target.value }))
                }
                className="mt-2"
              />
            </label>
            <label className="text-sm text-slate-300">
              Start date
              <Input
                type="date"
                value={sprintForm.startDate ?? ''}
                onChange={(event) =>
                  setSprintForm((prev) => ({ ...prev, startDate: event.target.value }))
                }
                className="mt-2"
              />
            </label>
            <label className="text-sm text-slate-300">
              End date
              <Input
                type="date"
                value={sprintForm.endDate ?? ''}
                onChange={(event) =>
                  setSprintForm((prev) => ({ ...prev, endDate: event.target.value }))
                }
                className="mt-2"
              />
            </label>
            <div className="md:col-span-4 flex justify-end">
              <Button type="submit" loading={createSprintMutation.isPending}>
                <Plus className="h-4 w-4" />
                Create sprint
              </Button>
            </div>
          </form>
        ) : (
          <p className="text-sm text-slate-400">
            You can follow sprint progress, but only admins or members can create or manage iterations.
          </p>
        )}

        {sprintsQuery.data?.length ? (
          <div className="grid gap-3 md:grid-cols-2">
            {sprintsQuery.data.map((sprint) => (
              <div
                key={sprint.sprintId}
                className="rounded-2xl border border-white/5 bg-white/5 p-4 text-sm text-slate-200"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-lg font-semibold text-white">{sprint.springName}</p>
                    <p className="text-xs text-slate-400">
                      {formatDate(sprint.startDate)} → {formatDate(sprint.endDate)}
                    </p>
                  </div>
                  <span className="rounded-full bg-white/10 px-3 py-1 text-xs uppercase text-white">
                    {sprint.sprintStatus}
                  </span>
                </div>
                {canManageBoard && (
                  <div className="mt-3 flex flex-wrap gap-2">
                    {sprint.sprintStatus === 'PLANNED' && (
                      <Button
                        variant="secondary"
                        onClick={() => startSprintMutation.mutate(sprint.sprintId)}
                        loading={startSprintMutation.isPending}
                      >
                        Start sprint
                      </Button>
                    )}
                    {sprint.sprintStatus === 'ACTIVE' && (
                      <Button
                        variant="secondary"
                        onClick={() => completeSprintMutation.mutate(sprint.sprintId)}
                        loading={completeSprintMutation.isPending}
                      >
                        Complete sprint
                      </Button>
                    )}
                  </div>
                )}
              </div>
            ))}
          </div>
        ) : (
          <EmptyState title="No sprints yet" description="Create a sprint above." />
        )}
      </GlassCard>
    </div>
  );
};

export default BoardDetailsPage;


