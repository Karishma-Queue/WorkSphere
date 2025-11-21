import { Link } from 'react-router-dom';
import { ArrowRight, CheckCircle2, LayoutDashboard, Shield, Users } from 'lucide-react';
import { GlassCard } from '../components/common/Card';

const highlights = [
  {
    icon: <LayoutDashboard className="h-5 w-5" />,
    title: 'Unified Delivery',
    description: 'Boards, issues, workflows and sprints aligned behind a single pane.'
  },
  {
    icon: <Users className="h-5 w-5" />,
    title: 'Role-aware Access',
    description: 'Respect backend authorization with admin, member and viewer policies.'
  },
  {
    icon: <Shield className="h-5 w-5" />,
    title: 'Secure by Design',
    description: 'JWT powered auth, guarded APIs and predictable client side state.'
  }
];

const LandingPage = () => {
  return (
    <div className="min-h-screen bg-[#030712] text-white">
      <header className="mx-auto flex max-w-6xl items-center justify-between px-6 py-6">
        <Link to="/" className="font-display text-2xl font-semibold tracking-tight">
          WorkSphere
        </Link>
        <div className="flex items-center gap-3 text-sm">
          <Link to="/login" className="text-slate-300 transition hover:text-white">
            Sign in
          </Link>
          <Link
            to="/signup"
            className="inline-flex items-center gap-2 rounded-xl bg-brand px-4 py-2 font-semibold text-white transition hover:bg-brand/90"
          >
            Create account
            <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
      </header>

      <main className="mx-auto flex max-w-6xl flex-col gap-12 px-6 py-10 lg:flex-row lg:items-center">
        <div className="flex-1 space-y-6">
          <p className="inline-flex items-center gap-2 rounded-full border border-white/10 px-4 py-1 text-xs uppercase tracking-widest text-slate-400">
            Work management OS
          </p>
          <h1 className="font-display text-4xl leading-tight text-white sm:text-5xl">
            Bring every board, workflow and request together inside a single workspace.
          </h1>
          <p className="text-lg text-slate-300">
            This frontend speaks only to the APIs provided by the WorkSphere backend. Ship faster with a
            clean, modern interface tailored for product and delivery teams.
          </p>
          <div className="flex flex-wrap gap-4">
            <Link
              to="/login"
              className="inline-flex items-center gap-2 rounded-2xl bg-brand px-6 py-3 text-base font-semibold text-white shadow-soft transition hover:bg-brand/85"
            >
              Launch workspace
              <ArrowRight className="h-4 w-4" />
            </Link>
            <Link
              to="/signup"
              className="inline-flex items-center gap-2 rounded-2xl border border-white/10 bg-white/5 px-6 py-3 text-base font-semibold text-white transition hover:border-white/30"
            >
              Request access
            </Link>
          </div>
          <div className="space-y-3 text-sm text-slate-300">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="h-4 w-4 text-brand" />
              Secure login, member management, sprints and workflows.
            </div>
            <div className="flex items-center gap-2">
              <CheckCircle2 className="h-4 w-4 text-brand" />
              Designed with Tailwind and modern glassmorphic touches.
            </div>
          </div>
        </div>

        <div className="flex-1 space-y-4">
          {highlights.map((item) => (
            <GlassCard key={item.title} className="space-y-2">
              <div className="inline-flex rounded-full bg-brand/10 p-2 text-brand">
                {item.icon}
              </div>
              <p className="text-lg font-semibold">{item.title}</p>
              <p className="text-sm text-slate-300">{item.description}</p>
            </GlassCard>
          ))}
        </div>
      </main>
    </div>
  );
};

export default LandingPage;


