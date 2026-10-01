import type { LucideIcon } from 'lucide-react'
import { cn } from '@/lib/utils'

export function StateMessage({
  icon: Icon,
  title,
  body,
  tone = 'muted',
  action,
}: {
  icon: LucideIcon
  title: string
  body?: string
  tone?: 'muted' | 'destructive'
  action?: React.ReactNode
}) {
  return (
    <div className="flex flex-col items-center justify-center gap-5 px-6 py-16 text-center" role="status">
      <div
        className={cn(
          'flex size-24 items-center justify-center rounded-full',
          tone === 'destructive' ? 'bg-destructive/15 text-destructive' : 'bg-muted text-muted-foreground',
        )}
      >
        <Icon aria-hidden="true" className="size-12" />
      </div>
      <div className="flex flex-col gap-2">
        <h2 className="text-2xl font-bold text-balance">{title}</h2>
        {body ? <p className="text-lg text-muted-foreground text-pretty">{body}</p> : null}
      </div>
      {action}
    </div>
  )
}

export function TileSkeletonGrid({ label, count = 8 }: { label: string; count?: number }) {
  return (
    <div role="status" aria-label={label}>
      <ul className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3" aria-hidden="true">
        {Array.from({ length: count }, (_, i) => (
          <li key={i} className="flex items-center gap-5 rounded-3xl bg-card p-5">
            <div className="size-20 shrink-0 animate-pulse rounded-2xl bg-muted" />
            <div className="flex flex-1 flex-col gap-3">
              <div className="h-6 w-2/3 animate-pulse rounded-lg bg-muted" />
              <div className="h-5 w-full animate-pulse rounded-lg bg-muted" />
              <div className="h-5 w-1/3 animate-pulse rounded-lg bg-muted" />
            </div>
          </li>
        ))}
      </ul>
    </div>
  )
}
