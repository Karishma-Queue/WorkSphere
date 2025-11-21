import { forwardRef, InputHTMLAttributes } from 'react';
import { cn } from '../../utils/style';

export const Input = forwardRef<HTMLInputElement, InputHTMLAttributes<HTMLInputElement>>(
  ({ className, ...props }, ref) => (
    <input
      ref={ref}
      className={cn(
        'w-full rounded-xl border border-white/10 bg-white/5 px-4 py-2.5 text-sm text-white placeholder:text-slate-500 focus:border-brand-400 focus:outline-none focus:ring-2 focus:ring-brand-500/30',
        className
      )}
      {...props}
    />
  )
);

Input.displayName = 'Input';

