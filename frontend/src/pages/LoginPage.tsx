import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { login } from '../api/auth';
import { useAuth } from '../context/AuthContext';
import { Input } from '../components/forms/Input';
import { FormField } from '../components/forms/FormField';
import { Button } from '../components/common/Button';
import toast from 'react-hot-toast';
import { GlassCard } from '../components/common/Card';
import { ShieldCheck } from 'lucide-react';

const LoginPage = () => {
  const navigate = useNavigate();
  const { login: persistSession } = useAuth();
  const [form, setForm] = useState({ email: '', password: '' });

  const loginMutation = useMutation({
    mutationFn: () => login(form),
    onSuccess: (data) => {
      persistSession(data);
      toast.success(`Welcome back, ${data.userName}`);
      navigate('/dashboard');
    },
    onError: (error: any) => {
      const message =
        error?.response?.data?.message ?? 'Unable to login. Please verify your credentials.';
      toast.error(message);
    }
  });

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    loginMutation.mutate();
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-slate-950 px-4 text-white">
      <GlassCard className="w-full max-w-md space-y-6">
        <div className="flex flex-col items-center gap-3 text-center">
          <div className="rounded-2xl bg-brand/10 p-3 text-brand">
            <ShieldCheck className="h-6 w-6" />
          </div>
          <div>
            <p className="text-xl font-semibold">Sign in to WorkSphere</p>
            <p className="text-sm text-slate-400">
              Continue with your organization email address.
            </p>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          <FormField label="Email">
            <Input
              type="email"
              autoComplete="email"
              required
              value={form.email}
              onChange={(event) => setForm((prev) => ({ ...prev, email: event.target.value }))}
            />
          </FormField>
          <FormField label="Password">
            <Input
              type="password"
              autoComplete="current-password"
              required
              value={form.password}
              onChange={(event) => setForm((prev) => ({ ...prev, password: event.target.value }))}
            />
          </FormField>
          <Button type="submit" className="w-full" loading={loginMutation.isPending}>
            Sign in
          </Button>
        </form>

        <p className="text-center text-sm text-slate-400">
          New to the workspace?{' '}
          <Link to="/signup" className="font-medium text-white">
            Create an account
          </Link>
        </p>
        <Link to="/" className="block text-center text-xs text-slate-500 hover:text-white">
          ← Back to overview
        </Link>
      </GlassCard>
    </div>
  );
};

export default LoginPage;


