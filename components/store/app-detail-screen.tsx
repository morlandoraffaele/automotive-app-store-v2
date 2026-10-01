'use client'

import {
  Bell,
  Car,
  ChevronRight,
  Contact,
  HardDrive,
  Layers,
  MapPin,
  Mic,
  PackageX,
  Phone,
  type LucideIcon,
} from 'lucide-react'
import Image from 'next/image'
import Link from 'next/link'
import { useState } from 'react'
import { formatDate } from '@/lib/i18n'
import { useApp } from '@/lib/store/hooks'
import type { AppState } from '@/lib/store/selectors'
import { useTranslation } from '@/lib/store/store-provider'
import type { AppListing, PermissionId } from '@/lib/store/types'
import { cn } from '@/lib/utils'
import { AppActionButton } from './app-action-button'
import { AppIcon } from './app-icon'
import { getStatusLabel, StatusChip, StatusProgressBar } from './app-status'
import { VersionChange } from './installed-screen'
import { StateMessage } from './state-views'
import { touchButtonVariants } from './touch-button'

const PERMISSION_ICONS: Record<PermissionId, LucideIcon> = {
  location: MapPin,
  microphone: Mic,
  contacts: Contact,
  'vehicle-data': Car,
  notifications: Bell,
  storage: HardDrive,
  phone: Phone,
}

type Tab = 'overview' | 'whats-new' | 'channels'

export function AppDetailScreen({ appId }: { appId: string }) {
  const { app, state, catalogStatus } = useApp(appId)
  const { t } = useTranslation()
  const [tab, setTab] = useState<Tab>('overview')

  if (catalogStatus === 'loading') {
    return <DetailSkeleton label={t('catalog.loading')} />
  }

  if (!app || !state) {
    return (
      <StateMessage
        icon={PackageX}
        title={t('detail.notFound')}
        body={t('detail.notFoundBody')}
        action={
          <Link href="/" className={touchButtonVariants()}>
            {t('installed.browse')}
          </Link>
        }
      />
    )
  }

  const tabs: { id: Tab; label: string }[] = [
    { id: 'overview', label: t('detail.overview') },
    { id: 'whats-new', label: t('detail.whatsNew') },
    { id: 'channels', label: t('detail.channels') },
  ]

  return (
    <div className="flex flex-col gap-6 p-6">
      <DetailHeader app={app} state={state} />

      <div role="tablist" aria-label={t('detail.tabs')} className="flex gap-2 rounded-3xl bg-card p-2">
        {tabs.map((item) => (
          <button
            key={item.id}
            id={`tab-${item.id}`}
            role="tab"
            type="button"
            aria-selected={tab === item.id}
            aria-controls={`panel-${item.id}`}
            onClick={() => setTab(item.id)}
            className={cn(
              'min-h-19 flex-1 rounded-2xl px-6 text-lg font-semibold transition-colors',
              tab === item.id ? 'bg-secondary text-foreground' : 'text-muted-foreground active:bg-secondary/60',
            )}
          >
            {item.label}
          </button>
        ))}
      </div>

      <div role="tabpanel" id={`panel-${tab}`} aria-labelledby={`tab-${tab}`}>
        {tab === 'overview' ? <OverviewPanel app={app} /> : null}
        {tab === 'whats-new' ? <WhatsNewPanel state={state} /> : null}
        {tab === 'channels' ? <ChannelsPanel app={app} state={state} /> : null}
      </div>
    </div>
  )
}

function DetailHeader({ app, state }: { app: AppListing; state: AppState }) {
  const { t } = useTranslation()
  const label = getStatusLabel(t, state.status, state.progress, state.targetVersion)
  const displayVersion = state.currentVersion ?? state.release?.version ?? ''

  return (
    <section className="flex flex-col gap-5 rounded-3xl bg-card p-6 md:flex-row md:items-center">
      <AppIcon icon={app.icon} color={app.iconColor} size="xl" />
      <div className="flex min-w-0 flex-1 flex-col gap-3">
        <div>
          <h2 className="text-4xl font-bold tracking-tight text-balance">{app.name}</h2>
          <p className="text-xl text-muted-foreground">{t('detail.developer', { name: app.developer })}</p>
        </div>
        <dl className="flex flex-wrap items-center gap-x-8 gap-y-2 text-lg">
          <div className="flex gap-2">
            <dt className="text-muted-foreground">{t('detail.version')}</dt>
            <dd>
              <VersionChange
                from={displayVersion}
                to={state.status === 'update-available' || state.status === 'downloading' ? state.targetVersion : null}
              />
            </dd>
          </div>
          <div className="flex gap-2">
            <dt className="text-muted-foreground">{t('detail.size')}</dt>
            <dd className="tabular-nums">{t('detail.sizeValue', { n: app.sizeMb })}</dd>
          </div>
          {state.channel ? (
            <div className="flex gap-2">
              <dt className="text-muted-foreground">{t('detail.channel')}</dt>
              <dd className={cn(state.channel.id !== 'stable' && 'font-semibold text-warning')}>{state.channel.name}</dd>
            </div>
          ) : null}
        </dl>
        <div className="flex flex-wrap items-center gap-3">
          <StatusChip status={state.status} progress={state.progress} label={label} />
        </div>
        <div className="max-w-xl">
          <StatusProgressBar status={state.status} progress={state.progress} />
        </div>
      </div>
      <AppActionButton appId={app.id} appName={app.name} state={state} className="md:min-w-56" />
    </section>
  )
}

function OverviewPanel({ app }: { app: AppListing }) {
  const { t } = useTranslation()
  return (
    <div className="flex flex-col gap-8">
      <p dir="auto" className="max-w-4xl text-xl leading-relaxed text-pretty">{app.description}</p>

      <section aria-labelledby="screenshots-heading" className="flex flex-col gap-4">
        <h3 id="screenshots-heading" className="text-2xl font-bold">
          {t('detail.screenshots')}
        </h3>
        <ul className="scrollbar-none -mx-6 flex gap-4 overflow-x-auto px-6">
          {app.screenshots.map((src, i) => (
            <li key={src} className="shrink-0">
              <Image
                src={src || '/placeholder.svg'}
                alt={t('detail.screenshotAlt', { name: app.name, n: i + 1 })}
                width={560}
                height={315}
                className="aspect-video w-[28rem] rounded-2xl bg-muted object-cover"
              />
            </li>
          ))}
        </ul>
      </section>

      <section aria-labelledby="permissions-heading" className="flex flex-col gap-4">
        <h3 id="permissions-heading" className="text-2xl font-bold">
          {t('detail.permissions')}
        </h3>
        <ul className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-4">
          {app.permissions.map((permission) => {
            const Icon = PERMISSION_ICONS[permission]
            return (
              <li key={permission} className="flex min-h-19 items-center gap-4 rounded-2xl bg-card px-5 text-lg font-medium">
                <Icon aria-hidden="true" className="size-7 text-muted-foreground" />
                {t(`permission.${permission}`)}
              </li>
            )
          })}
        </ul>
      </section>
    </div>
  )
}

function WhatsNewPanel({ state }: { state: AppState }) {
  const { t, locale } = useTranslation()
  const release = state.release
  if (!release) return null
  return (
    <section className="flex max-w-4xl flex-col gap-4 rounded-3xl bg-card p-6">
      <div className="flex flex-wrap items-baseline justify-between gap-3">
        <h3 className="text-2xl font-bold">{t('detail.newIn', { version: release.version })}</h3>
        <p className="text-lg text-muted-foreground">{t('detail.releasedOn', { date: formatDate(release.releaseDate, locale) })}</p>
      </div>
      <ul className="flex flex-col gap-3">
        {release.changelog.map((entry) => (
          <li key={entry} dir="auto" className="flex items-start gap-4 text-xl">
            <span aria-hidden="true" className="mt-3 size-2.5 shrink-0 rounded-full bg-primary" />
            {entry}
          </li>
        ))}
      </ul>
    </section>
  )
}

function ChannelsPanel({ app, state }: { app: AppListing; state: AppState }) {
  const { t } = useTranslation()
  return (
    <Link
      href={`/apps/${app.id}/channels`}
      className="flex max-w-4xl items-center gap-5 rounded-3xl bg-card p-6 transition-colors active:bg-secondary"
    >
      <div className="flex size-19 shrink-0 items-center justify-center rounded-2xl bg-primary/15 text-primary">
        <Layers aria-hidden="true" className="size-9" />
      </div>
      <div className="flex min-w-0 flex-1 flex-col gap-1">
        <p className="text-lg text-muted-foreground">{t('detail.activeChannel')}</p>
        <p className="text-2xl font-bold">
          {state.channel?.name}
          <span className="ms-3 font-mono text-lg font-normal text-muted-foreground">{state.release?.version}</span>
        </p>
        <p className="text-lg text-muted-foreground">{t('detail.channelsAvailable', { n: app.releases.length })}</p>
      </div>
      <span className="flex items-center gap-2 text-lg font-semibold text-primary">
        {t('detail.chooseChannel')}
        <ChevronRight aria-hidden="true" className="size-7 rtl:rotate-180" />
      </span>
    </Link>
  )
}

function DetailSkeleton({ label }: { label: string }) {
  return (
    <div role="status" aria-label={label} className="flex flex-col gap-6 p-6">
      <div className="flex items-center gap-6 rounded-3xl bg-card p-6" aria-hidden="true">
        <div className="size-32 animate-pulse rounded-[2rem] bg-muted" />
        <div className="flex flex-1 flex-col gap-3">
          <div className="h-9 w-1/3 animate-pulse rounded-lg bg-muted" />
          <div className="h-6 w-1/4 animate-pulse rounded-lg bg-muted" />
          <div className="h-6 w-1/2 animate-pulse rounded-lg bg-muted" />
        </div>
      </div>
      <div className="h-24 animate-pulse rounded-3xl bg-card" aria-hidden="true" />
    </div>
  )
}
