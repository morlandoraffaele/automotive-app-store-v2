'use client'

import { ArrowDown, Check, CircleAlert, LoaderCircle, type LucideIcon } from 'lucide-react'
import type { Translate } from '@/lib/i18n'
import type { AppStatus } from '@/lib/store/types'
import { cn } from '@/lib/utils'

type Tone = 'primary' | 'success' | 'destructive' | 'muted'

const STATUS_VISUALS: Record<Exclude<AppStatus, 'not-installed'>, { icon: LucideIcon | null; tone: Tone }> = {
  installed: { icon: Check, tone: 'success' },
  'up-to-date': { icon: Check, tone: 'success' },
  'update-available': { icon: ArrowDown, tone: 'primary' },
  downloading: { icon: null, tone: 'primary' },
  installing: { icon: LoaderCircle, tone: 'primary' },
  failed: { icon: CircleAlert, tone: 'destructive' },
}

const SOLID_TONE: Record<Tone, string> = {
  primary: 'bg-primary text-primary-foreground',
  success: 'bg-success text-success-foreground',
  destructive: 'bg-destructive text-destructive-foreground',
  muted: 'bg-muted text-muted-foreground',
}

const SOFT_TONE: Record<Tone, string> = {
  primary: 'bg-primary/15 text-primary',
  success: 'bg-success/15 text-success',
  destructive: 'bg-destructive/15 text-destructive',
  muted: 'bg-muted text-muted-foreground',
}

export function getStatusLabel(t: Translate, status: AppStatus, progress: number, version: string | null) {
  switch (status) {
    case 'update-available':
      return version ? t('status.update-available', { version }) : t('status.update-available.short')
    case 'downloading':
      return t('status.downloading', { n: Math.round(progress) })
    default:
      return t(`status.${status}`)
  }
}

export function ProgressRing({ progress, className }: { progress: number; className?: string }) {
  const radius = 15
  const circumference = 2 * Math.PI * radius
  return (
    <svg viewBox="0 0 36 36" className={cn('-rotate-90', className)} aria-hidden="true">
      <circle cx="18" cy="18" r={radius} fill="none" stroke="currentColor" strokeOpacity={0.3} strokeWidth="4" />
      <circle
        cx="18"
        cy="18"
        r={radius}
        fill="none"
        stroke="currentColor"
        strokeWidth="4"
        strokeLinecap="round"
        strokeDasharray={circumference}
        strokeDashoffset={circumference * (1 - progress / 100)}
        className="transition-[stroke-dashoffset] duration-200"
      />
    </svg>
  )
}

function StatusGlyph({ status, progress, className }: { status: AppStatus; progress: number; className?: string }) {
  if (status === 'not-installed') return null
  if (status === 'downloading') return <ProgressRing progress={progress} className={className} />
  const Icon = STATUS_VISUALS[status].icon
  if (!Icon) return null
  return (
    <Icon
      aria-hidden="true"
      strokeWidth={3}
      className={cn(className, status === 'installing' && 'animate-spin')}
    />
  )
}

/** Compact circular badge that sits on the corner of an app icon. */
export function StatusBadge({
  status,
  progress,
  label,
  className,
}: {
  status: AppStatus
  progress: number
  label: string
  className?: string
}) {
  if (status === 'not-installed') return null
  const { tone } = STATUS_VISUALS[status]
  return (
    <span
      role="img"
      aria-label={label}
      className={cn(
        'flex size-10 items-center justify-center rounded-full ring-4 ring-card',
        SOLID_TONE[tone],
        className,
      )}
    >
      <StatusGlyph status={status} progress={progress} className="size-6" />
    </span>
  )
}

/** Labeled pill used in list rows and the detail header. */
export function StatusChip({
  status,
  progress,
  label,
  className,
}: {
  status: AppStatus
  progress: number
  label: string
  className?: string
}) {
  if (status === 'not-installed') return null
  const { tone } = STATUS_VISUALS[status]
  return (
    <span
      className={cn(
        'inline-flex h-11 items-center gap-2 rounded-full px-4 text-base font-semibold tabular-nums',
        SOFT_TONE[tone],
        className,
      )}
    >
      <StatusGlyph status={status} progress={progress} className="size-5" />
      <span>{label}</span>
    </span>
  )
}

/** Linear progress for rows and the detail header while downloading. */
export function StatusProgressBar({ status, progress }: { status: AppStatus; progress: number }) {
  if (status !== 'downloading' && status !== 'installing') return null
  return (
    <div className="h-2 w-full overflow-hidden rounded-full bg-muted" aria-hidden="true">
      <div
        className={cn(
          'h-full rounded-full bg-primary transition-[width] duration-200',
          status === 'installing' && 'animate-pulse',
        )}
        style={{ width: `${status === 'installing' ? 100 : progress}%` }}
      />
    </div>
  )
}
