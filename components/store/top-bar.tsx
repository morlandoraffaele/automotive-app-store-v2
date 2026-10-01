'use client'

import { ChevronLeft, CircleArrowDown, CircleCheck, LoaderCircle, Wifi } from 'lucide-react'
import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { useSyncExternalStore } from 'react'
import { useUpdateSummary } from '@/lib/store/hooks'
import { useStoreRepository, useStoreSnapshot, useTranslation } from '@/lib/store/store-provider'
import type { StringKey } from '@/lib/i18n'
import { cn } from '@/lib/utils'
import { TouchButton, touchButtonVariants } from './touch-button'

function useScreenInfo(): { titleKey: StringKey | null; appId: string | null; backHref: string | null } {
  const pathname = usePathname()
  const match = pathname.match(/^\/apps\/([^/]+)(\/channels)?/)
  if (match) return { titleKey: null, appId: match[1], backHref: match[2] ? `/apps/${match[1]}` : '/' }
  if (pathname === '/installed') return { titleKey: 'installed.title', appId: null, backHref: null }
  if (pathname === '/settings') return { titleKey: 'settings.title', appId: null, backHref: null }
  return { titleKey: 'app.title', appId: null, backHref: null }
}

function subscribeClock(callback: () => void) {
  const id = setInterval(callback, 15_000)
  return () => clearInterval(id)
}

function Clock({ locale }: { locale: string }) {
  const time = useSyncExternalStore(
    subscribeClock,
    () => new Intl.DateTimeFormat(locale, { hour: 'numeric', minute: '2-digit' }).format(new Date()),
    () => '',
  )
  return (
    <span className="min-w-20 text-end text-xl font-semibold tabular-nums" suppressHydrationWarning>
      {time}
    </span>
  )
}

export function TopBar() {
  const { t, locale } = useTranslation()
  const repository = useStoreRepository()
  const snapshot = useStoreSnapshot()
  const { updatableIds, activeTaskCount } = useUpdateSummary()
  const { titleKey, appId, backHref } = useScreenInfo()

  const appName = appId ? snapshot.catalog.apps.find((a) => a.id === appId)?.name : null
  const title = titleKey ? t(titleKey) : (appName ?? '')
  const updateCount = updatableIds.length

  return (
    <header className="flex h-24 shrink-0 items-center gap-4 border-b border-border px-6">
      {backHref ? (
        <Link href={backHref} className={cn(touchButtonVariants({ variant: 'ghost', size: 'icon' }), '-ms-3')}>
          <ChevronLeft aria-hidden="true" className="rtl:rotate-180" />
          <span className="sr-only">{t('nav.back')}</span>
        </Link>
      ) : null}
      <h1 className="min-w-0 flex-1 truncate text-3xl font-bold tracking-tight text-balance">{title}</h1>

      <div className="flex items-center gap-3">
        <Link
          href="/installed"
          aria-live="polite"
          className={cn(
            'flex min-h-19 items-center gap-3 rounded-2xl px-5 text-lg font-semibold tabular-nums transition-colors',
            updateCount > 0 ? 'bg-primary/15 text-primary active:bg-primary/25' : 'text-muted-foreground active:bg-secondary',
          )}
        >
          {updateCount > 0 ? (
            <CircleArrowDown aria-hidden="true" className="size-7" />
          ) : (
            <CircleCheck aria-hidden="true" className="size-7 text-success" />
          )}
          <span className="hidden md:inline">
            {updateCount > 0 ? t('topbar.updatesAvailable', { n: updateCount }) : t('topbar.noUpdates')}
          </span>
          <span className="md:hidden">{updateCount}</span>
        </Link>

        {activeTaskCount > 0 ? (
          <span className={touchButtonVariants({ variant: 'secondary' })} role="status">
            <LoaderCircle aria-hidden="true" className="animate-spin" />
            {t('topbar.updating', { n: activeTaskCount })}
          </span>
        ) : (
          <TouchButton onClick={repository.updateAll} disabled={updateCount === 0}>
            {t('topbar.updateAll')}
          </TouchButton>
        )}

        <div className="hidden items-center gap-4 ps-3 text-muted-foreground lg:flex">
          <Wifi aria-label={t('topbar.wifi')} className="size-7" />
          <Clock locale={locale} />
        </div>
      </div>
    </header>
  )
}
