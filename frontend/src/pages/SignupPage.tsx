import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { signup, type SignupPayload } from '../api/auth';
import { Input } from '../components/forms/Input';
import { FormField } from '../components/forms/FormField';
import { Select } from '../components/forms/Select';
import { Button } from '../components/common/Button';
import { GlassCard } from '../components/common/Card';
import { Sparkles } from 'lucide-react';

const SignupPage = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState<SignupPayload>({
    user_name: '',
    email: '',
    password: '',
    role: 'MEMBER',
    job_title: '',
    department: '',
    profile_picture: null
  });

  const signupMutation = useMutation({
    mutationFn: () => signup(form),
    onSuccess: (data) => {
      toast.success(`Welcome to WorkSphere, ${data.userName}`);
      navigate('/login');
    },
    onError: (error: any) => {
      const errorMessage = error?.response?.data?.message || error?.message || 'Signup failed. Try again later.';
      if (errorMessage.includes('already') || errorMessage.includes('Email')) {
        toast.error('This email is already registered. Please use a different email or try logging in.');
      } else {
        toast.error(errorMessage);
      }
    }
  });

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    setForm((prev) => ({ ...prev, profile_picture: file ?? null }));
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    if (signupMutation.isPending) return; // Prevent double submission
    signupMutation.mutate();
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-slate-950 px-4 text-white">
      <GlassCard className="w-full max-w-2xl space-y-6">
        <div className="flex items-center gap-4">
          <div className="rounded-2xl bg-brand/10 p-3 text-brand">
            <Sparkles className="h-6 w-6" />
          </div>
          <div>
            <p className="text-xl font-semibold">Request workspace access</p>
            <p className="text-sm text-slate-400">
              Provide your details to help admins provision the right role.
            </p>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="grid gap-4 md:grid-cols-2">
          <FormField label="Full name" className="md:col-span-1">
            <Input
              required
              value={form.user_name}
              onChange={(event) => setForm((prev) => ({ ...prev, user_name: event.target.value }))}
            />
          </FormField>
          <FormField label="Email" className="md:col-span-1">
            <Input
              type="email"
              required
              value={form.email}
              onChange={(event) => setForm((prev) => ({ ...prev, email: event.target.value }))}
            />
          </FormField>
          <FormField label="Password" className="md:col-span-1">
            <Input
              type="password"
              minLength={6}
              required
              value={form.password}
              onChange={(event) => setForm((prev) => ({ ...prev, password: event.target.value }))}
            />
          </FormField>
          <FormField label="Role" className="md:col-span-1">
            <Select
              value={form.role}
              onChange={(event) =>
                setForm((prev) => ({ ...prev, role: event.target.value as SignupPayload['role'] }))
              }
            >
              <option value="ADMIN">Admin</option>
              <option value="MEMBER">Member</option>
              <option value="VIEWER">Viewer</option>
            </Select>
          </FormField>
          <FormField label="Job title" className="md:col-span-1">
            <Input
              required
              value={form.job_title}
              onChange={(event) => setForm((prev) => ({ ...prev, job_title: event.target.value }))}
            />
          </FormField>
          <FormField label="Department" className="md:col-span-1">
            <Input
              required
              value={form.department}
              onChange={(event) => setForm((prev) => ({ ...prev, department: event.target.value }))}
            />
          </FormField>
          <FormField label="Profile picture" hint="Optional" className="md:col-span-1">
            <Input type="file" accept="image/*" onChange={handleFileChange} />
          </FormField>
          <div className="md:col-span-2">
            <Button type="submit" className="w-full" loading={signupMutation.isPending}>
              Submit request
            </Button>
          </div>
        </form>

        <p className="text-center text-sm text-slate-400">
          Already invited?{' '}
          <Link to="/login" className="font-medium text-white">
            Sign in
          </Link>
        </p>
      </GlassCard>
    </div>
  );
};

export default SignupPage;


