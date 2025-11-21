import { useEffect, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import {
  createWorkflow,
  addStatus,
  addTransition,
  type WorkflowRequestDTO,
  type TransitionRequest
} from '../api/workflows';
import {
  createIssue,
  createSprint,
  type CreateIssueDTO,
  type CreateSprintDTO
} from '../api/issues';
import { getBoardsAsAdmin, getBoardsAsMember } from '../api/boards';
import { useAuth } from '../context/AuthContext';
import { SectionHeader } from '../components/common/SectionHeader';
import { GlassCard } from '../components/common/Card';
import { EmptyState } from '../components/common/EmptyState';
import { FormField } from '../components/forms/FormField';
import { Input } from '../components/forms/Input';
import { Select } from '../components/forms/Select';
import { Textarea } from '../components/forms/Textarea';
import { Button } from '../components/common/Button';
import type { BoardResponse, IssueType, Priority } from '../types/api';
import { ClipboardSignature, Hammer, Workflow, Repeat, Target } from 'lucide-react';

const issueTypes: IssueType[] = ['EPIC', 'STORY', 'TASK', 'BUG', 'SUB_TASK'];
const priorities: Priority[] = ['HIGHEST', 'HIGH', 'MEDIUM', 'LOW'];

const BuilderPage = () => {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const canBuild = user && user.role !== 'VIEWER';

  const [selectedBoardId, setSelectedBoardId] = useState('');
  const [workflowForm, setWorkflowForm] = useState<WorkflowRequestDTO>({
    workflow_name: '',
    issue: 'TASK',
    isDefault: false
  });
  type StatusFormState = {
    workflowId: string;
    started: boolean;
    ended: boolean;
    is_final: boolean;
    is_initial: boolean;
  };

  const [statusForm, setStatusForm] = useState<StatusFormState>({
    workflowId: '',
    started: false,
    ended: false,
    is_final: false,
    is_initial: false
  });
  const [statusName, setStatusName] = useState('');
  const [transitionForm, setTransitionForm] = useState({
    workflowId: '',
    from_status_id: '',
    to_status_id: '',
    allowedRoles: 'PROJECT_ADMIN,PROJECT_MEMBER'
  });
  const [issueForm, setIssueForm] = useState<CreateIssueDTO>({
    issue_type: 'TASK',
    priority: 'MEDIUM',
    summary: '',
    description: '',
    due_Date: ''
  });
  const [sprintForm, setSprintForm] = useState<CreateSprintDTO>({
    name: '',
    goal: '',
    startDate: '',
    endDate: ''
  });

  const adminBoards = useQuery({
    queryKey: ['builder', 'admin-boards', user?.userId],
    queryFn: () => getBoardsAsAdmin(user!.userId),
    enabled: Boolean(canBuild && user?.userId)
  });

  const memberBoards = useQuery({
    queryKey: ['builder', 'member-boards', user?.userId],
    queryFn: () => getBoardsAsMember(user!.userId),
    enabled: Boolean(canBuild && user?.userId)
  });

  const boards = useMemo(() => {
    const map = new Map<string, BoardResponse>();
    adminBoards.data?.forEach((b) => map.set(b.boardId, b));
    memberBoards.data?.forEach((b) => map.set(b.boardId, b));
    return Array.from(map.values());
  }, [adminBoards.data, memberBoards.data]);

  useEffect(() => {
    if (!selectedBoardId && boards.length) {
      setSelectedBoardId(boards[0].boardId);
    }
  }, [boards, selectedBoardId]);

  const workflowMutation = useMutation({
    mutationFn: () => createWorkflow(selectedBoardId, workflowForm),
    onSuccess: () => {
      toast.success('Workflow created');
      setWorkflowForm({ workflow_name: '', issue: 'TASK', isDefault: false });
      queryClient.invalidateQueries({ queryKey: ['workflows', selectedBoardId] });
    },
    onError: () => toast.error('Unable to create workflow')
  });

  const statusMutation = useMutation({
    mutationFn: () =>
      addStatus(statusForm.workflowId, {
        status_name: statusName,
        started: statusForm.started,
        ended: statusForm.ended,
        is_final: statusForm.is_final,
        is_initial: statusForm.is_initial
      }),
    onSuccess: () => {
      toast.success('Status added');
      setStatusName('');
      setStatusForm({
        workflowId: '',
        started: false,
        ended: false,
        is_final: false,
        is_initial: false
      });
    },
    onError: () => toast.error('Unable to add status')
  });

  const transitionMutation = useMutation({
    mutationFn: () =>
      addTransition(transitionForm.workflowId, {
        from_status_id: transitionForm.from_status_id,
        to_status_id: transitionForm.to_status_id,
        allowedRoles: transitionForm.allowedRoles
          .split(',')
          .map((role) => role.trim())
          .filter(Boolean)
      } as TransitionRequest),
    onSuccess: () => {
      toast.success('Transition added');
      setTransitionForm({
        workflowId: '',
        from_status_id: '',
        to_status_id: '',
        allowedRoles: 'PROJECT_ADMIN,PROJECT_MEMBER'
      });
    },
    onError: () => toast.error('Unable to add transition')
  });

  const issueMutation = useMutation({
    mutationFn: () => createIssue(selectedBoardId, issueForm),
    onSuccess: () => {
      toast.success('Issue created');
      setIssueForm({ issue_type: 'TASK', priority: 'MEDIUM', summary: '', description: '', due_Date: '' });
      queryClient.invalidateQueries({ queryKey: ['issues', selectedBoardId] });
    },
    onError: () => toast.error('Unable to create issue')
  });

  const sprintMutation = useMutation({
    mutationFn: () => createSprint(selectedBoardId, sprintForm),
    onSuccess: () => {
      toast.success('Sprint created');
      setSprintForm({ name: '', goal: '', startDate: '', endDate: '' });
      queryClient.invalidateQueries({ queryKey: ['sprints', selectedBoardId] });
    },
    onError: () => toast.error('Unable to create sprint')
  });

  if (!canBuild) {
    return (
      <div className="space-y-6">
        <SectionHeader
          title="Build center"
          description="This area is limited to admins and members."
          icon={<Hammer className="h-6 w-6" />}
        />
        <EmptyState
          title="Read-only access"
          description="Upgrade your workspace role to start creating workflows, issues and sprints."
        />
      </div>
    );
  }

  if (!boards.length) {
    return (
      <div className="space-y-6">
        <SectionHeader
          title="Build center"
          description="Create workflows, sprints, transitions and issues from a single hub."
          icon={<Hammer className="h-6 w-6" />}
        />
        <EmptyState
          title="No boards available"
          description="You need to be an admin or member of at least one board to proceed."
        />
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <SectionHeader
        title="Build center"
        description="Choose a board, then craft workflows, issues, statuses, transitions and sprints."
        icon={<Hammer className="h-6 w-6" />}
      />

      <GlassCard className="space-y-4">
        <FormField label="Working board">
          <Select value={selectedBoardId} onChange={(event) => setSelectedBoardId(event.target.value)}>
            {boards.map((board) => (
              <option key={board.boardId} value={board.boardId}>
                {board.boardName} ({board.boardKey})
              </option>
            ))}
          </Select>
        </FormField>
        <p className="text-xs text-slate-400">
          Actions below will run against the selected board. Switch anytime.
        </p>
      </GlassCard>

      <div className="grid gap-6 lg:grid-cols-2">
        <GlassCard className="space-y-4">
          <SectionHeader
            title="Create workflow"
            description="Spin up delivery workflows tailored to each issue type."
            icon={<Workflow className="h-5 w-5" />}
          />
          <form
            className="space-y-4"
            onSubmit={(event) => {
              event.preventDefault();
              workflowMutation.mutate();
            }}
          >
            <FormField label="Workflow name">
              <Input
                required
                value={workflowForm.workflow_name}
                onChange={(event) =>
                  setWorkflowForm((prev) => ({ ...prev, workflow_name: event.target.value }))
                }
              />
            </FormField>
            <FormField label="Issue type">
              <Select
                value={workflowForm.issue}
                onChange={(event) =>
                  setWorkflowForm((prev) => ({ ...prev, issue: event.target.value as IssueType }))
                }
              >
                {issueTypes.map((type) => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </Select>
            </FormField>
            <label className="inline-flex items-center gap-2 text-sm text-slate-300">
              <input
                type="checkbox"
                checked={workflowForm.isDefault}
                onChange={(event) =>
                  setWorkflowForm((prev) => ({ ...prev, isDefault: event.target.checked }))
                }
                className="h-4 w-4 rounded border-white/20 bg-transparent accent-brand"
              />
              Set as default workflow
            </label>
            <Button type="submit" loading={workflowMutation.isPending}>
              Create workflow
            </Button>
          </form>
        </GlassCard>

        <GlassCard className="space-y-4">
          <SectionHeader
            title="Create issue"
            description="Log work items, epics or bugs for the selected board."
            icon={<ClipboardSignature className="h-5 w-5" />}
          />
          <form
            className="grid gap-4"
            onSubmit={(event) => {
              event.preventDefault();
              issueMutation.mutate();
            }}
          >
            <FormField label="Summary">
              <Input
                required
                value={issueForm.summary}
                onChange={(event) =>
                  setIssueForm((prev) => ({ ...prev, summary: event.target.value }))
                }
              />
            </FormField>
            <div className="grid gap-4 md:grid-cols-2">
              <FormField label="Issue type">
                <Select
                  value={issueForm.issue_type}
                  onChange={(event) =>
                    setIssueForm((prev) => ({ ...prev, issue_type: event.target.value as IssueType }))
                  }
                >
                  {issueTypes.map((type) => (
                    <option key={type} value={type}>
                      {type}
                    </option>
                  ))}
                </Select>
              </FormField>
              <FormField label="Priority">
                <Select
                  value={issueForm.priority}
                  onChange={(event) =>
                    setIssueForm((prev) => ({ ...prev, priority: event.target.value as Priority }))
                  }
                >
                  {priorities.map((priority) => (
                    <option key={priority} value={priority}>
                      {priority}
                    </option>
                  ))}
                </Select>
              </FormField>
            </div>
            <FormField label="Due date">
              <Input
                type="date"
                value={issueForm.due_Date ?? ''}
                onChange={(event) =>
                  setIssueForm((prev) => ({ ...prev, due_Date: event.target.value }))
                }
              />
            </FormField>
            <FormField label="Description">
              <Textarea
                rows={3}
                value={issueForm.description ?? ''}
                onChange={(event) =>
                  setIssueForm((prev) => ({ ...prev, description: event.target.value }))
                }
              />
            </FormField>
            <Button type="submit" loading={issueMutation.isPending}>
              Create issue
            </Button>
          </form>
        </GlassCard>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <GlassCard className="space-y-4">
          <SectionHeader
            title="Add status"
            description="Enrich workflows with new states."
            icon={<Repeat className="h-5 w-5" />}
          />
          <form
            className="space-y-3"
            onSubmit={(event) => {
              event.preventDefault();
              statusMutation.mutate();
            }}
          >
            <FormField label="Workflow ID">
              <Input
                required
                value={statusForm.workflowId}
                onChange={(event) =>
                  setStatusForm((prev) => ({ ...prev, workflowId: event.target.value }))
                }
              />
            </FormField>
            <FormField label="Status name">
              <Input required value={statusName} onChange={(event) => setStatusName(event.target.value)} />
            </FormField>
            <div className="grid gap-2 text-sm text-slate-300 md:grid-cols-2">
              {[
                { key: 'is_initial', label: 'Initial status' },
                { key: 'is_final', label: 'Final status' },
                { key: 'started', label: 'Marks work start' },
                { key: 'ended', label: 'Marks work end' }
              ].map((flag) => (
                <label key={flag.key} className="inline-flex items-center gap-2">
                  <input
                    type="checkbox"
                    checked={Boolean((statusForm as any)[flag.key])}
                    onChange={(event) =>
                      setStatusForm((prev) => ({ ...prev, [flag.key]: event.target.checked }))
                    }
                    className="h-4 w-4 rounded border-white/20 bg-transparent accent-brand"
                  />
                  {flag.label}
                </label>
              ))}
            </div>
            <Button type="submit" loading={statusMutation.isPending}>
              Add status
            </Button>
          </form>
        </GlassCard>

        <GlassCard className="space-y-4">
          <SectionHeader
            title="Add transition"
            description="Connect statuses with guardrails."
            icon={<Target className="h-5 w-5" />}
          />
          <form
            className="space-y-3"
            onSubmit={(event) => {
              event.preventDefault();
              transitionMutation.mutate();
            }}
          >
            <FormField label="Workflow ID">
              <Input
                required
                value={transitionForm.workflowId}
                onChange={(event) =>
                  setTransitionForm((prev) => ({ ...prev, workflowId: event.target.value }))
                }
              />
            </FormField>
            <div className="grid gap-3 md:grid-cols-2">
              <FormField label="From status ID">
                <Input
                  required
                  value={transitionForm.from_status_id}
                  onChange={(event) =>
                    setTransitionForm((prev) => ({ ...prev, from_status_id: event.target.value }))
                  }
                />
              </FormField>
              <FormField label="To status ID">
                <Input
                  required
                  value={transitionForm.to_status_id}
                  onChange={(event) =>
                    setTransitionForm((prev) => ({ ...prev, to_status_id: event.target.value }))
                  }
                />
              </FormField>
            </div>
            <FormField label="Allowed roles" hint="Comma separated (e.g. PROJECT_ADMIN,PROJECT_MEMBER)">
              <Input
                value={transitionForm.allowedRoles}
                onChange={(event) =>
                  setTransitionForm((prev) => ({ ...prev, allowedRoles: event.target.value }))
                }
              />
            </FormField>
            <Button type="submit" loading={transitionMutation.isPending}>
              Add transition
            </Button>
          </form>
        </GlassCard>
      </div>

      <GlassCard className="space-y-4">
        <SectionHeader
          title="Create sprint"
          description="Plan the next iteration for this board."
          icon={<Hammer className="h-5 w-5" />}
        />
        <form
          className="grid gap-4 md:grid-cols-4"
          onSubmit={(event) => {
            event.preventDefault();
            sprintMutation.mutate();
          }}
        >
          <FormField label="Sprint name" className="md:col-span-1">
            <Input
              required
              value={sprintForm.name}
              onChange={(event) => setSprintForm((prev) => ({ ...prev, name: event.target.value }))}
            />
          </FormField>
          <FormField label="Goal" className="md:col-span-1">
            <Input
              value={sprintForm.goal ?? ''}
              onChange={(event) => setSprintForm((prev) => ({ ...prev, goal: event.target.value }))}
            />
          </FormField>
          <FormField label="Start date">
            <Input
              type="date"
              value={sprintForm.startDate ?? ''}
              onChange={(event) =>
                setSprintForm((prev) => ({ ...prev, startDate: event.target.value }))
              }
            />
          </FormField>
          <FormField label="End date">
            <Input
              type="date"
              value={sprintForm.endDate ?? ''}
              onChange={(event) => setSprintForm((prev) => ({ ...prev, endDate: event.target.value }))}
            />
          </FormField>
          <div className="md:col-span-4 flex justify-end">
            <Button type="submit" loading={sprintMutation.isPending}>
              Create sprint
            </Button>
          </div>
        </form>
      </GlassCard>
    </div>
  );
};

export default BuilderPage;


