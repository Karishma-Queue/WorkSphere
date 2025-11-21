import { ReactNode } from 'react';
import { cn } from '../../utils/style';

interface SectionHeaderProps {
  title: string;
  description?: string;
  icon?: ReactNode;
  actions?: ReactNode;
  className?: string;
}

export const SectionHeader = ({
  title,
  description,
  icon,
  actions,
  className
}: SectionHeaderProps) => {
  return (
    <div className={cn('flex flex-wrap items-center justify-between gap-4', className)}>
      <div className="flex items-center gap-3">
        {icon && <span className="text-brand-400">{icon}</span>}
        <div>
          <p className="font-semibold text-lg text-slate-100">{title}</p>
          {description && <p className="text-sm text-slate-400">{description}</p>}
        </div>
      </div>
      {actions}
    </div>
  );
};

