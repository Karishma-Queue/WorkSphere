import { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import {
  Search,
  LayoutDashboard,
  Users,
  FolderKanban,
  Compass,
  Shield,
  Sparkles
} from 'lucide-react';
import { getBoardsAsAdmin, getBoardsAsMember, searchBoards } from '../api/boards';
import { useAuth } from '../context/AuthContext';
import { SectionHeader } from '../components/common/SectionHeader';
import { Input } from '../components/forms/Input';
import { GlassCard } from '../components/common/Card';
import { EmptyState } from '../components/common/EmptyState';
import { formatDate } from '../utils/format';
import type { BoardResponse, Role } from '../types/api';

const BoardsGrid = ({ boards }: { boards?: BoardResponse[] }) => {
  if (!boards?.length) {
    return (
      <EmptyState
        title="No boards yet"
        description="Create requests or ask your admin to add you as a member."
      />
    );
  }

  return (
    <div className="grid gap-4 sm:grid-cols-2">
      {boards.map((board) => (
        <Link
          key={board.boardId}
          to={`/boards/${board.boardId}`}
          state={board}
          className="transition hover:-translate-y-1 hover:brightness-110"
        >
          <GlassCard className="h-full space-y-3">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm uppercase tracking-wide text-slate-400">{board.boardKey}</p>
                <p className="text-xl font-semibold text-white">{board.boardName}</p>
              </div>
              <div className="rounded-full bg-brand/10 p-2 text-brand">
                <LayoutDashboard className="h-5 w-5" />
              </div>
            </div>
            <p className="text-sm text-slate-300 line-clamp-2">{board.description ?? '—'}</p>
            <div className="flex items-center justify-between text-xs text-slate-500">
              <span>Created by {board.createdByUserName ?? 'N/A'}</span>
              <span>{formatDate(board.createdAt)}</span>
            </div>
          </GlassCard>
        </Link>
      ))}
    </div>
  );
};

const roleSpotlight: Record<
  Role,
  { title: string; description: string; cta?: { label: string; to: string }; icon: React.ReactNode }
> = {
  ADMIN: {
    title: 'Admin control center',
    description:
      'Approve workspace board requests, seed workflows and keep governance tight across every delivery train.',
    cta: { label: 'Review requests', to: '/requests' },
    icon: <Shield className="h-5 w-5" />
  },
  MEMBER: {
    title: 'Project builder mode',
    description:
      'Jump into build center to craft workflows, create sprints or raise new issues for your boards.',
    cta: { label: 'Open build center', to: '/builder' },
    icon: <Sparkles className="h-5 w-5" />
  },
  VIEWER: {
    title: 'Observer overview',
    description:
      'Explore boards shared with you. Upgrade to member when you are ready to contribute and create work.',
    icon: <Compass className="h-5 w-5" />
  }
};

const DashboardPage = () => {
  const { user } = useAuth();
  const [searchTerm, setSearchTerm] = useState('');
  const currentRole = user?.role ?? 'VIEWER';

  const adminBoards = useQuery({
    queryKey: ['boards', 'admin', user?.userId],
    queryFn: () => getBoardsAsAdmin(user!.userId),
    enabled: Boolean(user?.userId),
    placeholderData: []
  });

  const memberBoards = useQuery({
    queryKey: ['boards', 'member', user?.userId],
    queryFn: () => getBoardsAsMember(user!.userId),
    enabled: Boolean(user?.userId),
    placeholderData: []
  });

  const searchQuery = useQuery({
    queryKey: ['boards', 'search', searchTerm],
    queryFn: () => searchBoards(searchTerm),
    enabled: searchTerm.length >= 2
  });

  const stats = useMemo(
    () => [
      {
        label: 'Admin boards',
        value: adminBoards.data?.length ?? 0,
        icon: <LayoutDashboard className="h-5 w-5" />
      },
      {
        label: 'Member boards',
        value: memberBoards.data?.length ?? 0,
        icon: <Users className="h-5 w-5" />
      },
      {
        label: 'Discoverable',
        value: searchQuery.data?.length ?? 0,
        icon: <Compass className="h-5 w-5" />
      }
    ],
    [adminBoards.data?.length, memberBoards.data?.length, searchQuery.data?.length]
  );

  const showingSearch = searchTerm.length >= 2;

  return (
    <div className="space-y-10">
      <div className="space-y-6">
        <SectionHeader
          title="Workspace overview"
          description="Monitor every board you administer, collaborate on or want to join."
          icon={<FolderKanban className="h-6 w-6" />}
        />
        <GlassCard className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div className="flex items-center gap-3">
            <div className="rounded-2xl bg-brand/10 p-3 text-brand">{roleSpotlight[currentRole].icon}</div>
            <div>
              <p className="text-lg font-semibold text-white">{roleSpotlight[currentRole].title}</p>
              <p className="text-sm text-slate-400">{roleSpotlight[currentRole].description}</p>
            </div>
          </div>
          {roleSpotlight[currentRole].cta && (
            <Link
              to={roleSpotlight[currentRole].cta!.to}
              className="rounded-xl bg-brand px-5 py-2 text-sm font-semibold text-white"
            >
              {roleSpotlight[currentRole].cta!.label}
            </Link>
          )}
        </GlassCard>
      </div>

      <div className="grid gap-4 sm:grid-cols-3">
        {stats.map((stat) => (
          <GlassCard key={stat.label} className="flex items-center gap-3">
            <div className="rounded-2xl bg-brand/10 p-3 text-brand">{stat.icon}</div>
            <div>
              <p className="text-sm text-slate-400">{stat.label}</p>
              <p className="text-2xl font-semibold text-white">{stat.value}</p>
            </div>
          </GlassCard>
        ))}
      </div>

      <div className="space-y-4">
        <div className="flex flex-col gap-3 rounded-2xl border border-white/5 bg-white/5 p-4 backdrop-blur">
          <label className="text-sm font-medium text-slate-200" htmlFor="board-search">
            Search boards
          </label>
          <div className="relative">
            <Search className="pointer-events-none absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500" />
            <Input
              id="board-search"
              placeholder="Search by name or key..."
              className="pl-11"
              value={searchTerm}
              onChange={(event) => setSearchTerm(event.target.value)}
            />
          </div>
          <p className="text-xs text-slate-500">
            Showing {showingSearch ? 'matching results' : 'boards you manage and collaborate on'}.
          </p>
        </div>

        {showingSearch ? (
          <BoardsGrid boards={searchQuery.data} />
        ) : (
          <div className="space-y-8">
            <div className="space-y-3">
              <SectionHeader
                title="Boards you administer"
                description="Full control to configure workflows, sprints and people."
              />
              <BoardsGrid boards={adminBoards.data} />
            </div>
            <div className="space-y-3">
              <SectionHeader
                title="Boards you collaborate on"
                description="Track progress on projects where you are a member."
              />
              <BoardsGrid boards={memberBoards.data} />
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default DashboardPage;


