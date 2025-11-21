import { Link } from 'react-router-dom';
import { GlassCard } from '../components/common/Card';

const NotFoundPage = () => {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-slate-950 px-4 text-white">
      <GlassCard className="w-full max-w-md space-y-4 text-center">
        <p className="text-sm uppercase tracking-[0.2em] text-slate-400">404</p>
        <h1 className="text-3xl font-semibold text-white">Page not found</h1>
        <p className="text-sm text-slate-400">
          The page you are looking for does not exist. Return to the dashboard or go home.
        </p>
        <div className="flex flex-wrap justify-center gap-3">
          <Link
            to="/dashboard"
            className="rounded-xl bg-brand px-4 py-2 text-sm font-semibold text-white"
          >
            Dashboard
          </Link>
          <Link
            to="/"
            className="rounded-xl border border-white/10 px-4 py-2 text-sm font-semibold text-white"
          >
            Home
          </Link>
        </div>
      </GlassCard>
    </div>
  );
};

export default NotFoundPage;


