import { Link, NavLink } from 'react-router-dom';
import { Button } from '../components/common/Button';
import { useAuth } from '../context/AuthContext';
import { cn } from '../utils/style';

export const MainLayout = ({ children }: { children: React.ReactNode }) => {
  const { user, logout } = useAuth();

  const roleNavItems = (() => {
    if (!user) return [];
    const items = [{ path: '/dashboard', label: 'Workspace' }];
    if (user.role !== 'VIEWER') {
      items.push({ path: '/builder', label: 'Build Center' });
    }
    if (user.role === 'ADMIN') {
      items.push({ path: '/requests', label: 'Requests' });
    }
    items.push({ path: '/profile', label: 'Profile' });
    return items;
  })();

  return (
    <div className="min-h-screen bg-[#010409] text-white">
      <header className="sticky top-0 z-30 border-b border-white/5 bg-slate-950/80 backdrop-blur-lg">
        <div className="mx-auto flex max-w-6xl items-center justify-between gap-6 px-6 py-4">
          <Link to="/" className="font-display text-xl font-semibold text-white">
            WorkSphere
          </Link>

          {user && (
            <nav className="hidden gap-6 text-sm text-slate-400 md:flex">
              {roleNavItems.map((item) => (
                <NavLink
                  key={item.path}
                  to={item.path}
                  className={({ isActive }) =>
                    cn('transition hover:text-white', isActive && 'text-white')
                  }
                >
                  {item.label}
                </NavLink>
              ))}
            </nav>
          )}

          <div className="flex items-center gap-4">
            {user ? (
              <>
                <div className="text-right">
                  <p className="text-sm font-semibold">{user.userName}</p>
                  <p className="text-xs text-slate-400">{user.role}</p>
                </div>
                <Button variant="secondary" onClick={logout}>
                  Logout
                </Button>
              </>
            ) : (
              <Link to="/login" className="text-sm font-semibold text-white hover:text-brand">
                Sign in
              </Link>
            )}
          </div>
        </div>
      </header>

      <main className="mx-auto w-full max-w-6xl px-6 py-10">{children}</main>
    </div>
  );
};

