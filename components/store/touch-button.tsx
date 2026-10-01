import { cva, type VariantProps } from 'class-variance-authority'
import { cn } from '@/lib/utils'

/** Every interactive element is at least 76px tall to meet driver-distraction guidelines. */
export const touchButtonVariants = cva(
  'inline-flex min-h-19 shrink-0 items-center justify-center gap-3 rounded-2xl px-7 text-lg font-semibold whitespace-nowrap transition-colors select-none disabled:pointer-events-none disabled:opacity-50 aria-disabled:pointer-events-none aria-disabled:opacity-50 [&_svg]:size-7 [&_svg]:shrink-0',
  {
    variants: {
      variant: {
        primary: 'bg-primary text-primary-foreground active:bg-primary/80',
        secondary: 'bg-secondary text-secondary-foreground active:bg-secondary/70',
        outline: 'border-2 border-border bg-transparent text-foreground active:bg-secondary',
        ghost: 'bg-transparent text-foreground active:bg-secondary',
        destructive: 'bg-destructive text-destructive-foreground active:bg-destructive/80',
        warning: 'bg-warning text-warning-foreground active:bg-warning/80',
      },
      size: {
        default: '',
        icon: 'min-w-19 px-0',
        wide: 'min-w-56',
      },
    },
    defaultVariants: { variant: 'primary', size: 'default' },
  },
)

export type TouchButtonVariants = VariantProps<typeof touchButtonVariants>

export function TouchButton({
  className,
  variant,
  size,
  type = 'button',
  ...props
}: React.ComponentProps<'button'> & TouchButtonVariants) {
  return <button type={type} className={cn(touchButtonVariants({ variant, size }), className)} {...props} />
}
