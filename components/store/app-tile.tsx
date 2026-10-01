'use client'

import Link from 'next/link'
import type { AppState } from '@/lib/store/selectors'
import type { AppListing } from '@/lib/store/types'
import { useTranslation } from '@/lib/store/store-provider'
import { cn } from '@/lib/utils'
import { AppIcon } from './app-icon'
import { getStatusLabel, StatusBadge, StatusProgressBar } from './app-status'

export function AppTile({ app, state }: { app: AppListing; state: AppState }) {
  const { t } = useTranslation()
  const statusLabel = getStatusLabel(t, state.status, state.progress, state.targetVersion)
  const showStatusText = state.status !== 'not-installed' && state.status !== 'up-to-date'

  return (
    <Link
      href={`/apps/${app.id}`}
      className="flex h-full items-center gap-5 rounded-3xl border-2 border-transparent bg-card p-5 transition-colors active:border-primary"
    >
      <div className="relative shrink-0">
        <AppIcon icon={app.icon} color={app.iconColor} />
        <StatusBadge
          status={state.status}
          progress={state.progress}
          label={statusLabel}
          className="absolute -end-2.5 -bottom-2.5"
        />
      </div>
      <div className="flex min-w-0 flex-1 flex-col gap-1">
        <h3 className="truncate text-xl font-bold">{app.name}</h3>
        <p className="truncate text-base text-muted-foreground">{app.tagline}</p>
        <div className="flex items-center gap-2 pt-1 text-sm font-semibold">
          <span className="rounded-full bg-muted px-3 py-1 text-muted-foreground">{t(`category.${app.category}`)}</span>
          {showStatusText ? (
            <span
              className={cn(
                'truncate tabular-nums',
                state.status === 'failed' ? 'text-destructive' : state.status === 'installed' ? 'text-success' : 'text-primary',
              )}
            >
              {statusLabel}
            </span>
          ) : null}
        </div>
        {state.status === 'downloading' || state.status === 'installing' ? (
          <div className="pt-2">
            <StatusProgressBar status={state.status} progress={state.progress} />
          </div>
        ) : null}
      </div>
    </Link>
  )
}
