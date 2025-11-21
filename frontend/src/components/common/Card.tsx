import { ReactNode } from 'react';
import { cn } from '../../utils/style';

interface CardProps {
  children: ReactNode;
  className?: string;
}

export const GlassCard = ({ children, className }: CardProps) => (
  <div
    className={cn(
      'rounded-2xl border border-white/5 bg-white/5 backdrop-blur-xl shadow-soft p-6',
      className
    )}
  >
    {children}
  </div>
);

