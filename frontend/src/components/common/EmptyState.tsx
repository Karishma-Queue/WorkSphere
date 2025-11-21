import { ReactNode } from 'react';
import { cn } from '../../utils/style';

interface EmptyStateProps {
  title: string;
  description?: string;
  icon?: ReactNode;
  className?: string;
}

export const EmptyState = ({ title, description, icon, className }: EmptyStateProps) => (
  <div
    className={cn(
      'flex flex-col items-center justify-center gap-2 rounded-xl border border-dashed border-white/10 p-6 text-center text-slate-400',
      className
    )}
  >
    {icon}
    <p className="font-medium text-slate-200">{title}</p>
    {description && <p className="text-sm text-slate-400">{description}</p>}
  </div>
);

