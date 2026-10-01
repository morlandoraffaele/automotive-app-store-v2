'use client'

import { ArrowRight, SquareStack } from 'lucide-react'
import Link from 'next/link'
import { getAppState, type AppState } from '@/lib/store/selectors'
import { useStoreSnapshot, useTranslation } from '@/lib/store/store-provider'
import type { AppListing } from '@/lib/store/types'
import { cn } from '@/lib/utils'
import { AppActionButton } from './app-action-button'
import { AppIcon } from './app-icon'
import { getStatusLabel, StatusBadge, StatusChip, StatusProgressBar } from './app-status'
import { StateMessage, TileSkeletonGrid } from './state-views'
import { touchButtonVariants } from './touch-button'

const NEEDS_ATTENTION = new Set(['update-available', 'downloading', 'installing', 'failed'])

export function VersionChange({ from, to, className }: { from: string | null; to: string | null; className?: string }) {
  if (!from) return null
  return (
    <span className={cn('inline-flex items-center gap-2 font-mono tabular-nums', className)}>
      <span>{from}</span>
      {to && to !== from ? (
        <>
          <ArrowRight aria-hidden="true" className="size-5 rtl:rotate-180" />
          <span className="sr-only">{'→'}</span>
          <span className="font-semibold text-primary">{to}</span>
        </>
      ) : null}
    </span>
  )
}

function InstalledRow({ app, state }: { app: AppListing; state: AppState }) {
  const { t } = useTranslation()
  const label = getStatusLabel(t, state.status, state.progress, state.targetVersion)

  return (
    <li className="flex items-center gap-5 rounded-3xl bg-card p-4 ps-5">
      <Link href={`/apps/${app.id}`} className="flex min-h-19 min-w-0 flex-1 items-center gap-5 rounded-2xl">
        <div className="relative shrink-0">
          <AppIcon icon={app.icon} color={app.iconColor} />
          <StatusBadge status={state.status} progress={state.progress} label={label} className="absolute -end-2.5 -bottom-2.5" />
        </div>
        <div className="flex min-w-0 flex-1 flex-col gap-1.5">
          <div className="flex flex-wrap items-center gap-x-3 gap-y-1">
            <h3 className="truncate text-xl font-bold">{app.name}</h3>
            {state.channel && state.channel.id !== 'stable' ? (
              <span className="rounded-full border-2 border-warning px-2.5 py-0.5 text-sm font-semibold text-warning">
                {state.channel.name}
              </span>
            ) : null}
          </div>
          <VersionChange
            from={state.currentVersion}
            to={state.status === 'up-to-date' || state.status === 'installed' ? null : state.targetVersion}
            className="text-lg text-muted-foreground"
          />
          <StatusProgressBar status={state.status} progress={state.progress} />
        </div>
      </Link>
      <StatusChip status={state.status} progress={state.progress} label={label} className="hidden lg:inline-flex" />
      <AppActionButton appId={app.id} appName={app.name} state={state} />
    </li>
  )
}

export function InstalledScreen() {
  const snapshot = useStoreSnapshot()
  const { t } = useTranslation()

  if (snapshot.catalog.status === 'loading') {
    return (
      <div className="p-6">
        <TileSkeletonGrid label={t('catalog.loading')} count={4} />
      </div>
    )
  }

  const installed = snapshot.catalog.apps
    .filter((app) => snapshot.installed[app.id] || snapshot.tasks[app.id]?.kind === 'update')
    .map((app) => ({ app, state: getAppState(snapshot, app) }))

  if (installed.length === 0) {
    return (
      <StateMessage
        icon={SquareStack}
        title={t('installed.emptyTitle')}
        body={t('installed.emptyBody')}
        action={
          <Link href="/" className={touchButtonVariants()}>
            {t('installed.browse')}
          </Link>
        }
      />
    )
  }

  const needsAttention = installed.filter(({ state }) => NEEDS_ATTENTION.has(state.status))
  const upToDate = installed.filter(({ state }) => !NEEDS_ATTENTION.has(state.status))

  return (
    <div className="flex flex-col gap-8 p-6">
      {needsAttention.length > 0 ? (
        <section aria-labelledby="updates-heading" className="flex flex-col gap-4">
          <h2 id="updates-heading" className="text-2xl font-bold">
            {t('installed.updatesSection', { n: needsAttention.length })}
          </h2>
          <ul className="flex flex-col gap-3">
            {needsAttention.map(({ app, state }) => (
              <InstalledRow key={app.id} app={app} state={state} />
            ))}
          </ul>
        </section>
      ) : null}
      {upToDate.length > 0 ? (
        <section aria-labelledby="uptodate-heading" className="flex flex-col gap-4">
          <h2 id="uptodate-heading" className="text-2xl font-bold">
            {t('installed.upToDateSection', { n: upToDate.length })}
          </h2>
          <ul className="flex flex-col gap-3">
            {upToDate.map(({ app, state }) => (
              <InstalledRow key={app.id} app={app} state={state} />
            ))}
          </ul>
        </section>
      ) : null}
    </div>
  )
}
