import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import {
  createBoardRequest,
  getAdminBoardRequests,
  getAdminBoardRequestsByStatus,
  getMyBoardRequests,
  approveBoardRequest,
  rejectBoardRequest
} from '../api/boardRequests';
import { SectionHeader } from '../components/common/SectionHeader';
import { GlassCard } from '../components/common/Card';
import { Input } from '../components/forms/Input';
import { Textarea } from '../components/forms/Textarea';
import { Select } from '../components/forms/Select';
import { Button } from '../components/common/Button';
import { EmptyState } from '../components/common/EmptyState';
import type { BoardDetailsDTO, RequestStatus } from '../types/api';
import { ClipboardList, ShieldAlert, CheckCircle, FilePlus2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { formatDate } from '../utils/format';

const requestStatuses: RequestStatus[] = ['PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'];

const RequestCard = ({
  request,
  onApprove,
  onReject
}: {
  request: BoardDetailsDTO;
  onApprove?: () => void;
  onReject?: (reason: string) => void;
}) => (
  <div className="rounded-2xl border border-white/5 bg-white/5 p-4 text-sm text-slate-200">
    <div className="flex items-center justify-between gap-4">
      <div>
        <p className="text-lg font-semibold text-white">{request.board_request_name}</p>
        <p className="text-xs text-slate-400">Key {request.board_request_key}</p>
      </div>
      <span className="rounded-full bg-white/10 px-3 py-1 text-xs uppercase text-white">
        {request.status}
      </span>
    </div>
    <p className="mt-3 text-slate-300">{request.description}</p>
    <p className="mt-1 text-xs text-slate-400">Reason: {request.justification}</p>
    <p className="mt-2 text-xs text-slate-500">Requested {formatDate(request.requestedAt)}</p>
    {request.rejection_reason && (
      <p className="mt-1 text-xs text-rose-300">Rejection: {request.rejection_reason}</p>
    )}
    {(onApprove || onReject) && (
      <div className="mt-4 flex flex-wrap gap-3">
        {onApprove && (
          <Button variant="secondary" onClick={onApprove}>
            Approve
          </Button>
        )}
        {onReject && (
          <Button
            variant="ghost"
            onClick={() => {
              const reason = window.prompt('Provide a rejection reason');
              if (reason) onReject(reason);
            }}
          >
            Reject
          </Button>
        )}
      </div>
    )}
  </div>
);

const RequestsPage = () => {
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const [form, setForm] = useState({
    board_request_name: '',
    board_request_key: '',
    description: '',
    justification: ''
  });
  const [myFilter, setMyFilter] = useState<RequestStatus | 'ALL'>('ALL');
  const [adminFilter, setAdminFilter] = useState<RequestStatus | 'ALL'>('ALL');

  const createRequestMutation = useMutation({
    mutationFn: () => createBoardRequest(form),
    onSuccess: () => {
      toast.success('Request submitted');
      setForm({
        board_request_name: '',
        board_request_key: '',
        description: '',
        justification: ''
      });
      queryClient.invalidateQueries({ queryKey: ['board-requests', 'mine'] });
    },
    onError: () => toast.error('Unable to submit request')
  });

  const myRequestsQuery = useQuery({
    queryKey: ['board-requests', 'mine', myFilter],
    queryFn: () => getMyBoardRequests(myFilter === 'ALL' ? undefined : myFilter),
    enabled: Boolean(user)
  });

  const adminRequestsQuery = useQuery({
    queryKey: ['board-requests', 'admin', adminFilter],
    queryFn: () =>
      adminFilter === 'ALL'
        ? getAdminBoardRequests()
        : getAdminBoardRequestsByStatus(adminFilter),
    enabled: user?.role === 'ADMIN'
  });

  const approveMutation = useMutation({
    mutationFn: (id: string) => approveBoardRequest(id),
    onSuccess: () => {
      toast.success('Request approved');
      queryClient.invalidateQueries({ queryKey: ['board-requests', 'admin'] });
      queryClient.invalidateQueries({ queryKey: ['board-requests', 'mine'] });
    },
    onError: () => toast.error('Unable to approve request')
  });

  const rejectMutation = useMutation({
    mutationFn: ({ id, reason }: { id: string; reason: string }) =>
      rejectBoardRequest(id, { rejection_reason: reason }),
    onSuccess: () => {
      toast.success('Request rejected');
      queryClient.invalidateQueries({ queryKey: ['board-requests', 'admin'] });
      queryClient.invalidateQueries({ queryKey: ['board-requests', 'mine'] });
    },
    onError: () => toast.error('Unable to reject request')
  });

  const canManageRequests = user?.role === 'ADMIN';

  const myRequests = myRequestsQuery.data ?? [];
  const adminRequests = adminRequestsQuery.data ?? [];

  const pendingCount = useMemo(
    () => adminRequests.filter((req) => req.status === 'PENDING').length,
    [adminRequests]
  );

  return (
    <div className="space-y-10">
      <SectionHeader
        title="Board requests"
        description="Create, monitor and approve workspace board requests."
        icon={<ClipboardList className="h-6 w-6" />}
      />

      <GlassCard className="space-y-4">
        <SectionHeader
          title="Request a new board"
          description="Share the essentials so admins can approve fast."
          icon={<FilePlus2 className="h-5 w-5" />}
        />
        <form
          className="grid gap-4 md:grid-cols-2"
          onSubmit={(event) => {
            event.preventDefault();
            createRequestMutation.mutate();
          }}
        >
          <label className="text-sm text-slate-300">
            Board name
            <Input
              required
              value={form.board_request_name}
              onChange={(event) =>
                setForm((prev) => ({ ...prev, board_request_name: event.target.value }))
              }
              className="mt-2"
            />
          </label>
          <label className="text-sm text-slate-300">
            Key
            <Input
              required
              value={form.board_request_key}
              onChange={(event) =>
                setForm((prev) => ({ ...prev, board_request_key: event.target.value.toUpperCase() }))
              }
              className="mt-2 uppercase tracking-widest"
            />
          </label>
          <label className="text-sm text-slate-300 md:col-span-2">
            Description
            <Textarea
              rows={3}
              required
              value={form.description}
              onChange={(event) => setForm((prev) => ({ ...prev, description: event.target.value }))}
              className="mt-2"
            />
          </label>
          <label className="text-sm text-slate-300 md:col-span-2">
            Justification
            <Textarea
              rows={2}
              required
              value={form.justification}
              onChange={(event) =>
                setForm((prev) => ({ ...prev, justification: event.target.value }))
              }
              className="mt-2"
            />
          </label>
          <div className="md:col-span-2 flex justify-end">
            <Button type="submit" loading={createRequestMutation.isPending}>
              Submit request
            </Button>
          </div>
        </form>
      </GlassCard>

      <div className="grid gap-6 lg:grid-cols-2">
        <GlassCard className="space-y-4">
          <SectionHeader
            title="My requests"
            description="Track the status of every board you requested."
            icon={<CheckCircle className="h-5 w-5" />}
            actions={
              <Select
                value={myFilter}
                onChange={(event) => setMyFilter(event.target.value as RequestStatus | 'ALL')}
                className="w-32"
              >
                <option value="ALL">All</option>
                {requestStatuses.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </Select>
            }
          />
          {myRequests.length ? (
            <div className="space-y-3">
              {myRequests.map((request) => (
                <RequestCard key={request.board_request_id} request={request} />
              ))}
            </div>
          ) : (
            <EmptyState title="No requests yet" description="Submit your first board request above." />
          )}
        </GlassCard>

        {canManageRequests && (
          <GlassCard className="space-y-4">
            <SectionHeader
              title="Admin review queue"
              description={`${pendingCount} request(s) pending review.`}
              icon={<ShieldAlert className="h-5 w-5" />}
              actions={
                <Select
                  value={adminFilter}
                  onChange={(event) =>
                    setAdminFilter(event.target.value as RequestStatus | 'ALL')
                  }
                  className="w-32"
                >
                  <option value="ALL">All</option>
                  {requestStatuses.map((status) => (
                    <option key={status} value={status}>
                      {status}
                    </option>
                  ))}
                </Select>
              }
            />
            {adminRequests.length ? (
              <div className="space-y-3">
                {adminRequests.map((request) => (
                  <RequestCard
                    key={request.board_request_id}
                    request={request}
                    onApprove={() => approveMutation.mutate(request.board_request_id)}
                    onReject={(reason) => rejectMutation.mutate({ id: request.board_request_id, reason })}
                  />
                ))}
              </div>
            ) : (
              <EmptyState
                title="No requests to review"
                description="New submissions will show up here instantly."
              />
            )}
          </GlassCard>
        )}
      </div>
    </div>
  );
};

export default RequestsPage;


