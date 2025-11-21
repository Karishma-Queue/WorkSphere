import { ReactNode } from 'react';
import { cn } from '../../utils/style';

interface FormFieldProps {
  label: string;
  htmlFor?: string;
  hint?: string;
  action?: ReactNode;
  children: ReactNode;
  error?: string;
  className?: string;
}

export const FormField = ({
  label,
  htmlFor,
  hint,
  action,
  children,
  error,
  className
}: FormFieldProps) => (
  <label className={cn('flex flex-col gap-2 text-sm text-slate-200', className)} htmlFor={htmlFor}>
    <div className="flex items-center justify-between gap-3">
      <span className="font-medium">{label}</span>
      {action}
    </div>
    {children}
    <div className="text-xs text-slate-400">{error ?? hint}</div>
  </label>
);

