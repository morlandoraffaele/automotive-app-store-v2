'use client'

import { LoaderCircle, RefreshCw } from 'lucide-react'
import { LOCALE_META } from '@/lib/i18n'
import { useStoreRepository, useStoreSnapshot, useTranslation } from '@/lib/store/store-provider'
import type { CatalogSimulation, Locale, ThemeMode } from '@/lib/store/types'
import { cn } from '@/lib/utils'
import { ProgressRing } from './app-status'

function SettingsSection({ title, children }: { title: string; children: React.ReactNode }) {
  const id = `settings-${title.replace(/\s+/g, '-').toLowerCase()}`
  return (
    <section aria-labelledby={id} className="flex flex-col gap-3">
      <h2 id={id} className="px-2 text-lg font-semibold text-muted-foreground">
        {title}
      </h2>
      <div className="flex flex-col divide-y divide-border overflow-hidden rounded-3xl bg-card">{children}</div>
    </section>
  )
}

function ToggleRow({
  label,
  description,
  checked,
  onChange,
}: {
  label: string
  description: string
  checked: boolean
  onChange: (value: boolean) => void
}) {
  return (
    <button
      type="button"
      role="switch"
      aria-checked={checked}
      onClick={() => onChange(!checked)}
      className="flex min-h-24 w-full items-center gap-6 px-6 text-start transition-colors active:bg-secondary"
    >
      <span className="flex flex-1 flex-col gap-1">
        <span className="text-xl font-semibold">{label}</span>
        <span className="text-lg text-muted-foreground">{description}</span>
      </span>
      <span
        aria-hidden="true"
        className={cn(
          'flex h-12 w-22 shrink-0 items-center rounded-full p-1.5 transition-colors',
          checked ? 'bg-primary' : 'bg-muted-foreground/40',
        )}
      >
        <span
          className={cn(
            'size-9 rounded-full bg-background shadow transition-transform',
            checked && 'translate-x-10 rtl:-translate-x-10',
          )}
        />
      </span>
    </button>
  )
}

function SegmentedRow<T extends string>({
  label,
  value,
  options,
  onChange,
}: {
  label: string
  value: T
  options: { value: T; label: string }[]
  onChange: (value: T) => void
}) {
  return (
    <div className="flex min-h-24 flex-col gap-3 px-6 py-3 md:flex-row md:items-center md:gap-6">
      <span className="flex-1 text-xl font-semibold">{label}</span>
      <div role="radiogroup" aria-label={label} className="flex gap-1 rounded-2xl bg-muted p-1.5">
        {options.map((option) => (
          <button
            key={option.value}
            type="button"
            role="radio"
            aria-checked={value === option.value}
            onClick={() => onChange(option.value)}
            className={cn(
              'min-h-16 min-w-32 rounded-xl px-5 text-lg font-semibold transition-colors',
              value === option.value ? 'bg-card text-foreground shadow' : 'text-muted-foreground',
            )}
          >
            {option.label}
          </button>
        ))}
      </div>
    </div>
  )
}

function StoreUpdateRows() {
  const { storeUpdate } = useStoreSnapshot()
  const repository = useStoreRepository()
  const { t } = useTranslation()
  const busy = storeUpdate.phase === 'checking' || storeUpdate.phase === 'downloading'

  const statusText =
    storeUpdate.phase === 'checking'
      ? t('settings.storeChecking')
      : storeUpdate.phase === 'downloading'
        ? t('settings.storeDownloading', { version: storeUpdate.availableVersion ?? '', n: storeUpdate.progress })
        : storeUpdate.phase === 'ready' && storeUpdate.availableVersion
          ? t('settings.storeReady', { version: storeUpdate.availableVersion })
          : t('settings.storeUpToDate')

  return (
    <>
      <div className="flex min-h-24 items-center gap-6 px-6">
        <span className="flex flex-1 flex-col gap-1">
          <span className="text-xl font-semibold">{t('settings.storeVersion')}</span>
          <span
            aria-live="polite"
            className={cn('text-lg', storeUpdate.phase === 'ready' ? 'font-semibold text-primary' : 'text-muted-foreground')}
          >
            {statusText}
          </span>
        </span>
        <span className="font-mono text-xl tabular-nums">{storeUpdate.currentVersion}</span>
      </div>
      <button
        type="button"
        onClick={repository.checkForStoreUpdate}
        disabled={busy}
        className="flex min-h-24 w-full items-center gap-5 px-6 text-start text-xl font-semibold text-primary transition-colors active:bg-secondary disabled:opacity-70"
      >
        {storeUpdate.phase === 'downloading' ? (
          <ProgressRing progress={storeUpdate.progress} className="size-8" />
        ) : storeUpdate.phase === 'checking' ? (
          <LoaderCircle aria-hidden="true" className="size-8 animate-spin" />
        ) : (
          <RefreshCw aria-hidden="true" className="size-8" />
        )}
        {t('settings.checkUpdate')}
      </button>
    </>
  )
}

export function SettingsScreen() {
  const { settings } = useStoreSnapshot()
  const repository = useStoreRepository()
  const { t } = useTranslation()

  return (
    <div className="mx-auto flex w-full max-w-5xl flex-col gap-8 p-6 pb-10">
      <SettingsSection title={t('settings.updates')}>
        <ToggleRow
          label={t('settings.autoUpdate')}
          description={t('settings.autoUpdateDesc')}
          checked={settings.autoUpdate}
          onChange={(autoUpdate) => repository.updateSettings({ autoUpdate })}
        />
        <ToggleRow
          label={t('settings.wifiOnly')}
          description={t('settings.wifiOnlyDesc')}
          checked={settings.wifiOnly}
          onChange={(wifiOnly) => repository.updateSettings({ wifiOnly })}
        />
      </SettingsSection>

      <SettingsSection title={t('settings.about')}>
        <StoreUpdateRows />
      </SettingsSection>

      <SettingsSection title={t('settings.display')}>
        <SegmentedRow<ThemeMode>
          label={t('settings.theme')}
          value={settings.theme}
          onChange={(theme) => repository.updateSettings({ theme })}
          options={[
            { value: 'day', label: t('settings.day') },
            { value: 'night', label: t('settings.night') },
          ]}
        />
        <SegmentedRow<Locale>
          label={t('settings.language')}
          value={settings.locale}
          onChange={(locale) => repository.updateSettings({ locale })}
          options={(Object.keys(LOCALE_META) as Locale[]).map((value) => ({ value, label: LOCALE_META[value].label }))}
        />
      </SettingsSection>

      <SettingsSection title={t('settings.demo')}>
        <SegmentedRow<CatalogSimulation>
          label={t('settings.simulation')}
          value={settings.simulation}
          onChange={(simulation) => repository.updateSettings({ simulation })}
          options={(['normal', 'loading', 'empty', 'error'] as const).map((value) => ({
            value,
            label: t(`settings.sim.${value}`),
          }))}
        />
      </SettingsSection>
    </div>
  )
}
