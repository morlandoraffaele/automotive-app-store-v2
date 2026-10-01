'use client'

import { CircleCheck, Lock, PackageX, TriangleAlert } from 'lucide-react'
import Link from 'next/link'
import { useState } from 'react'
import { formatDate, type StringKey } from '@/lib/i18n'
import { useApp } from '@/lib/store/hooks'
import { useStoreRepository, useStoreSnapshot, useTranslation } from '@/lib/store/store-provider'
import type { ChannelDefinition, ChannelRelease } from '@/lib/store/types'
import { cn } from '@/lib/utils'
import { AppActionButton } from './app-action-button'
import { StateMessage } from './state-views'
import { TouchButton, touchButtonVariants } from './touch-button'

interface ChannelOption {
  channel: ChannelDefinition
  release: ChannelRelease
}

export function ChannelsScreen({ appId }: { appId: string }) {
  const { app, state, catalogStatus } = useApp(appId)
  const { channels } = useStoreSnapshot()
  const repository = useStoreRepository()
  const { t, locale } = useTranslation()
  const [pending, setPending] = useState<ChannelDefinition | null>(null)
  const [justSwitched, setJustSwitched] = useState<string | null>(null)

  if (catalogStatus === 'loading') {
    return <div className="p-6" role="status" aria-label={t('catalog.loading')} />
  }

  if (!app || !state || !state.channel) {
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

  const activeChannel = state.channel
  const options: ChannelOption[] = channels
    .map((channel) => ({ channel, release: app.releases.find((r) => r.channelId === channel.id) }))
    .filter((o): o is ChannelOption => Boolean(o.release))
    .sort((a, b) => a.channel.stabilityRank - b.channel.stabilityRank)

  const commitSwitch = (channel: ChannelDefinition) => {
    repository.switchChannel(app.id, channel.id)
    setPending(null)
    setJustSwitched(channel.id)
  }

  const handleSelect = (channel: ChannelDefinition) => {
    if (channel.locked || channel.id === activeChannel.id) return
    setJustSwitched(null)
    if (channel.stabilityRank > activeChannel.stabilityRank) {
      setPending(channel)
    } else {
      commitSwitch(channel)
    }
  }

  const switchedChannel = justSwitched === activeChannel.id ? activeChannel : null

  return (
    <div className="flex flex-col gap-6 p-6 pb-10">
      <div>
        <h2 className="text-2xl font-bold">{t('channels.title', { name: app.name })}</h2>
        <p className="text-lg text-muted-foreground">{t('channels.subtitle')}</p>
      </div>

      {pending ? (
        <section
          role="alert"
          aria-labelledby="channel-warning-title"
          className="flex flex-col gap-5 rounded-3xl border-2 border-warning bg-warning/10 p-6 lg:flex-row lg:items-center"
        >
          <TriangleAlert aria-hidden="true" className="size-12 shrink-0 text-warning" />
          <div className="flex flex-1 flex-col gap-1">
            <h3 id="channel-warning-title" className="text-2xl font-bold">
              {t('channels.warnTitle', { name: pending.name })}
            </h3>
            <p className="text-lg text-pretty">
              {t('channels.warnBody', { name: pending.name, current: activeChannel.name })}
            </p>
          </div>
          <div className="flex flex-wrap gap-3">
            <TouchButton variant="secondary" onClick={() => setPending(null)}>
              {t('channels.cancel', { name: activeChannel.name })}
            </TouchButton>
            <TouchButton variant="warning" onClick={() => commitSwitch(pending)}>
              {t('channels.confirm')}
            </TouchButton>
          </div>
        </section>
      ) : null}

      {switchedChannel ? (
        <section
          role="status"
          className="flex flex-col gap-4 rounded-3xl bg-primary/10 p-5 ps-6 md:flex-row md:items-center"
        >
          <CircleCheck aria-hidden="true" className="size-9 shrink-0 text-primary" />
          <p className="flex-1 text-lg font-semibold">
            {state.status === 'update-available' && state.targetVersion
              ? t('channels.switchedUpdate', { name: switchedChannel.name, version: state.targetVersion })
              : t('channels.switched', { name: switchedChannel.name })}
          </p>
          {state.status !== 'up-to-date' && state.status !== 'installed' ? (
            <AppActionButton appId={app.id} appName={app.name} state={state} />
          ) : null}
        </section>
      ) : null}

      <div role="radiogroup" aria-label={t('detail.channels')} className="flex flex-col gap-3">
        {options.map(({ channel, release }) => {
          const active = channel.id === activeChannel.id
          const isPending = pending?.id === channel.id
          return (
            <div
              key={channel.id}
              role="radio"
              aria-checked={active}
              aria-disabled={channel.locked || undefined}
              tabIndex={channel.locked ? -1 : 0}
              onClick={() => handleSelect(channel)}
              onKeyDown={(e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                  e.preventDefault()
                  handleSelect(channel)
                }
              }}
              className={cn(
                'flex cursor-pointer flex-col gap-4 rounded-3xl border-2 bg-card p-6 transition-colors lg:flex-row lg:items-start',
                active ? 'border-primary' : isPending ? 'border-warning' : 'border-transparent active:bg-secondary',
                channel.locked && 'cursor-not-allowed opacity-70',
              )}
            >
              <span
                aria-hidden="true"
                className={cn(
                  'mt-1 flex size-10 shrink-0 items-center justify-center rounded-full border-4',
                  active ? 'border-primary' : 'border-border',
                )}
              >
                {channel.locked ? (
                  <Lock className="size-5 text-muted-foreground" />
                ) : active ? (
                  <span className="size-4 rounded-full bg-primary" />
                ) : null}
              </span>

              <div className="flex min-w-0 flex-1 flex-col gap-2">
                <div className="flex flex-wrap items-center gap-3">
                  <h3 className="text-2xl font-bold">{channel.name}</h3>
                  <span
                    className={cn(
                      'rounded-full px-3 py-1 text-sm font-semibold',
                      channel.stabilityRank === 0 ? 'bg-success/15 text-success' : 'bg-warning/15 text-warning',
                    )}
                  >
                    {t(`channels.stabilityRank.${Math.min(channel.stabilityRank, 3)}` as StringKey)}
                  </span>
                  {active ? (
                    <span className="rounded-full bg-primary px-3 py-1 text-sm font-bold text-primary-foreground">
                      {t('channels.active')}
                    </span>
                  ) : null}
                  {channel.locked ? (
                    <span className="inline-flex items-center gap-1.5 rounded-full bg-muted px-3 py-1 text-sm font-semibold text-muted-foreground">
                      <Lock aria-hidden="true" className="size-4" />
                      {t('channels.locked')}
                    </span>
                  ) : null}
                </div>
                <p dir="auto" className="text-lg text-muted-foreground text-pretty">{channel.description}</p>
                {channel.locked && channel.lockReason ? (
                  <p className="flex items-center gap-2 text-lg font-medium">
                    <Lock aria-hidden="true" className="size-5 shrink-0 text-muted-foreground" />
                    {channel.lockReason}
                  </p>
                ) : null}
                <ul className="flex flex-col gap-1 pt-1">
                  {release.changelog.slice(0, 2).map((entry) => (
                    <li key={entry} className="flex items-start gap-3 text-lg">
                      <span aria-hidden="true" className="mt-2.5 size-2 shrink-0 rounded-full bg-muted-foreground" />
                      {entry}
                    </li>
                  ))}
                </ul>
              </div>

              <dl className="flex shrink-0 flex-row gap-6 lg:flex-col lg:items-end lg:gap-1 lg:text-end">
                <div>
                  <dt className="sr-only">{t('detail.version')}</dt>
                  <dd className="font-mono text-xl font-semibold tabular-nums">
                    {t('channels.latest', { version: release.version })}
                  </dd>
                </div>
                <div>
                  <dt className="sr-only">{t('detail.releasedOn', { date: '' })}</dt>
                  <dd className="text-lg text-muted-foreground">
                    {t('detail.releasedOn', { date: formatDate(release.releaseDate, locale) })}
                  </dd>
                </div>
              </dl>
            </div>
          )
        })}
      </div>
    </div>
  )
}
