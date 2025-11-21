import { ButtonHTMLAttributes } from 'react';
import { cn } from '../../utils/style';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'ghost';
  loading?: boolean;
}

export const Button = ({
  children,
  className,
  variant = 'primary',
  disabled,
  loading,
  ...props
}: ButtonProps) => {
  const base =
    'inline-flex items-center justify-center gap-2 rounded-xl px-4 py-2.5 text-sm font-semibold transition-all focus:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-offset-slate-950';
  const variants = {
    primary: 'bg-brand text-white hover:bg-brand-500 focus-visible:ring-brand-300',
    secondary:
      'bg-white/10 text-white hover:bg-white/20 focus-visible:ring-white/40 border border-white/10',
    ghost: 'bg-transparent text-slate-300 hover:text-white'
  };

  return (
    <button
      className={cn(base, variants[variant], disabled && 'opacity-60', className)}
      disabled={disabled || loading}
      {...props}
    >
      {loading && (
        <span className="h-4 w-4 animate-spin rounded-full border-2 border-white/60 border-t-transparent" />
      )}
      {children}
    </button>
  );
};

