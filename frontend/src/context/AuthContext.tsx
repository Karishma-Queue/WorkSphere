import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import type { LoginResponse, Role } from '../types/api';

type AuthState = (LoginResponse & { expiresAt?: number }) | null;

interface AuthContextValue {
  user: AuthState;
  isAuthenticated: boolean;
  login: (payload: LoginResponse, remember?: boolean) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const STORAGE_KEY = 'worksphere.session';

const readStoredSession = (): AuthState => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw) as AuthState;
    if (parsed?.expiresAt && parsed.expiresAt < Date.now()) {
      localStorage.removeItem(STORAGE_KEY);
      return null;
    }
    return parsed;
  } catch {
    return null;
  }
};

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<AuthState>(() => readStoredSession());

  useEffect(() => {
    if (user) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, [user]);

  const contextValue = useMemo<AuthContextValue>(
    () => ({
      user,
      isAuthenticated: Boolean(user?.token),
      login: (payload, remember = true) => {
        setUser({
          ...payload,
          expiresAt: remember ? Date.now() + 1000 * 60 * 60 * 24 * 7 : undefined
        });
      },
      logout: () => setUser(null)
    }),
    [user]
  );

  return <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return ctx;
};

export const userHasRole = (role: Role, allowed: Role[]) => allowed.includes(role);

