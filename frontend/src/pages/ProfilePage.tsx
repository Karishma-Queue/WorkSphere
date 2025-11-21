import { useEffect, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { SectionHeader } from '../components/common/SectionHeader';
import { GlassCard } from '../components/common/Card';
import { Input } from '../components/forms/Input';
import { Textarea } from '../components/forms/Textarea';
import { Button } from '../components/common/Button';
import { getUserProfile, updateUserProfile, type UpdateUserPayload } from '../api/user';
import { useAuth } from '../context/AuthContext';
import { formatDate } from '../utils/format';
import { UserRound, UploadCloud } from 'lucide-react';

const ProfilePage = () => {
  const { user, logout } = useAuth();
  const queryClient = useQueryClient();
  const [form, setForm] = useState<UpdateUserPayload>({
    userName: '',
    jobTitle: '',
    department: '',
    image: null
  });

  const profileQuery = useQuery({
    queryKey: ['profile', user?.userId],
    queryFn: () => getUserProfile(user!.userId),
    enabled: Boolean(user?.userId)
  });

  useEffect(() => {
    if (profileQuery.data) {
      setForm({
        userName: profileQuery.data.userName,
        jobTitle: profileQuery.data.jobTitle ?? '',
        department: profileQuery.data.department ?? '',
        image: null
      });
    }
  }, [profileQuery.data]);

  const updateMutation = useMutation({
    mutationFn: () => updateUserProfile(user!.userId, form),
    onSuccess: (data) => {
      toast.success('Profile updated');
      queryClient.setQueryData(['profile', user?.userId], data);
    },
    onError: () => toast.error('Unable to update profile')
  });

  const avatarFallback = useMemo(
    () => form.userName?.split(' ').map((part) => part[0]).join('').slice(0, 2).toUpperCase() ?? 'WS',
    [form.userName]
  );

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    setForm((prev) => ({ ...prev, image: file ?? null }));
  };

  return (
    <div className="space-y-10">
      <SectionHeader title="Profile" description="Manage your account and workspace identity." icon={<UserRound className="h-6 w-6" />} />

      <GlassCard className="space-y-6">
        <div className="flex flex-wrap items-center gap-6">
          <div className="flex h-20 w-20 items-center justify-center rounded-2xl bg-brand/10 text-2xl font-semibold text-brand">
            {profileQuery.data?.profilePictureUrl ? (
              <img
                src={profileQuery.data.profilePictureUrl}
                alt={profileQuery.data.userName}
                className="h-20 w-20 rounded-2xl object-cover"
              />
            ) : (
              avatarFallback
            )}
          </div>
          <div className="space-y-1">
            <p className="text-xl font-semibold text-white">{profileQuery.data?.userName}</p>
            <p className="text-sm text-slate-400">{profileQuery.data?.email ?? user?.email}</p>
            <p className="text-xs text-slate-500">
              Joined {formatDate(profileQuery.data?.createdAt)} • Role {profileQuery.data?.role}
            </p>
          </div>
        </div>

        <form
          className="grid gap-4 md:grid-cols-2"
          onSubmit={(event) => {
            event.preventDefault();
            updateMutation.mutate();
          }}
        >
          <label className="text-sm text-slate-300">
            Full name
            <Input
              required
              value={form.userName}
              onChange={(event) => setForm((prev) => ({ ...prev, userName: event.target.value }))}
              className="mt-2"
            />
          </label>
          <label className="text-sm text-slate-300">
            Job title
            <Input
              value={form.jobTitle ?? ''}
              onChange={(event) => setForm((prev) => ({ ...prev, jobTitle: event.target.value }))}
              className="mt-2"
            />
          </label>
          <label className="text-sm text-slate-300 md:col-span-2">
            Department / Squad
            <Textarea
              rows={2}
              value={form.department ?? ''}
              onChange={(event) =>
                setForm((prev) => ({ ...prev, department: event.target.value }))
              }
              className="mt-2"
            />
          </label>
          <label className="text-sm text-slate-300 md:col-span-2">
            Profile picture
            <div className="mt-2 flex flex-col gap-3 rounded-xl border border-dashed border-white/10 p-4 text-center">
              <UploadCloud className="mx-auto h-6 w-6 text-brand" />
              <p className="text-xs text-slate-400">
                Upload PNG or JPEG, 2MB max. Current file:{' '}
                {form.image ? form.image.name : 'None selected'}
              </p>
              <Input type="file" accept="image/*" onChange={handleFileChange} className="border-0" />
            </div>
          </label>
          <div className="md:col-span-2 flex flex-wrap justify-between gap-3">
            <Button type="submit" loading={updateMutation.isPending}>
              Save changes
            </Button>
            <Button type="button" variant="ghost" onClick={logout}>
              Sign out
            </Button>
          </div>
        </form>
      </GlassCard>
    </div>
  );
};

export default ProfilePage;


